/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.optimization.constraints;

import static net.sf.opendse.encoding.firm.variables.Variables.p;
import static net.sf.opendse.encoding.firm.variables.Variables.var;
import static net.sf.opendse.model.Models.filterCommunications;
import static net.sf.opendse.model.Models.getLinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.optimization.SpecificationWrapper;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Model;
import org.opt4j.satdecoding.Term;

import com.google.inject.Inject;

public class SpecificationCapacityConstraints extends AbstractSpecificationConstraints implements SpecificationConstraints {

	protected final Specification specification;
	protected final Set<CapacityConstraint<?>> capacityConstraints = new HashSet<CapacityConstraint<?>>();

	@Inject
	public SpecificationCapacityConstraints(SpecificationWrapper specification) {
		super();
		this.specification = specification.getSpecification();
		this.capacityConstraints.addAll(getCapacityConstraints(this.specification));

		initConstraints();
	}

	protected void initConstraints() {
		Architecture<Resource, Link> architecture = specification.getArchitecture();
		Routings<Task, Resource, Link> routings = specification.getRoutings();

		for (CapacityConstraint<?> capacityConstraint : capacityConstraints) {
			Element element = capacityConstraint.getElement();
			String attribute = capacityConstraint.getAttribute();
			Map<Element, Integer> elements = capacityConstraint.getElements();

			ParameterObject cap = getParameter(element, attribute + CAPACITY);
			ParameterObject min = getParameter(element, attribute + CAPACITY_MIN);
			ParameterObject max = getParameter(element, attribute + CAPACITY_MAX);

			if (cap.isNull() && max.isNull() && min.isNull()) {
				throw new IllegalArgumentException("No capacity bound set for " + element + " with attribute "
						+ attribute);
			}

			if (min.isNull() && max.isNull()) {
				max = cap;
			}

			List<Term> terms = new ArrayList<Term>();

			for (Entry<Element, Integer> entry : elements.entrySet()) {
				Element e = entry.getKey();
				int value = entry.getValue();
				if (e instanceof Mapping) {
					Term term = new Term(value, p(e));
					terms.add(term);
				} else if (e instanceof Task) { // assume its a
												// communication task
					Task task = (Task) e;
					Architecture<Resource, Link> routing = routings.get(task);
					if (element instanceof Resource && routing.containsVertex((Resource) element)) {
						Term term = new Term(value, p(var(task, (Resource) element)));
						terms.add(term);
					} else if (element instanceof Link && routing.containsEdge((Link) element)) {
						for (DirectedLink lrr : getLinks(architecture, (Link) element)) {
							Term term = new Term(value, p(var(task, lrr)));
							terms.add(term);
						}
					}
				}
			}

			// System.out.println(elements);
			// System.out.println(terms);

			// TODO Scale?
			//Integer scale = element.getAttribute(attribute + CAPACITY_SCALE);

			if (!max.isNull()) {
				Constraint cmax = new Constraint("<=", 0);
				cmax.addAll(terms);

				if (!max.isParameter()) {
					cmax.setRhs(max.getInteger());
				} else {
					Constraint eq = new Constraint("=", 0);
					for (Term term : getParameterTerms(element, max)) {
						Term t = new Term(-term.getCoefficient(), term.getLiteral());
						cmax.add(t);
						eq.add(1, term.getLiteral());
					}
					eq.add(-1, p(element));
					constraints.add(eq);
				}
				constraints.add(cmax);
			}

			if (!min.isNull()) {
				Constraint cmin = new Constraint(">=", 0);
				cmin.addAll(terms);

				if (!min.isParameter()) {
					cmin.add(-min.getInteger(), p(element));
				} else {
					Constraint eq = new Constraint("=", 0);
					for (Term term : getParameterTerms(element, min)) {
						Term t = new Term(-term.getCoefficient(), term.getLiteral());
						cmin.add(t);
						eq.add(1, term.getLiteral());
					}
					eq.add(-1, p(element));
					constraints.add(eq);
				}
				constraints.add(cmin);
			}
		}
	}

	protected Set<CapacityConstraint<?>> getCapacityConstraints(Specification specification) {
		Set<CapacityConstraint<?>> capacityConstraints = new HashSet<CapacityConstraint<?>>();

		Architecture<Resource, Link> architecture = specification.getArchitecture();
		Application<Task, Dependency> application = specification.getApplication();
		Mappings<Task, Resource> mappings = specification.getMappings();
		Routings<Task, Resource, Link> routings = specification.getRoutings();

		Set<Element> allElements = new HashSet<Element>();
		allElements.addAll(architecture.getVertices());
		allElements.addAll(architecture.getEdges());

		for (Element e : allElements) {
			Set<String> attributeNames = e.getAttributeNames();

			Set<String> visited = new HashSet<String>();

			for (String attributeName : attributeNames) {
				if (isCapacity(attributeName)) {
					String name = getName(attributeName);

					if (!visited.contains(name)) {
						visited.add(name);

						String action = e.getAttribute(name + CAPACITY_ACTION);

						boolean bM = (action != null) ? action.contains("M") : true;
						boolean bT = (action != null) ? action.contains("T") : true;
						boolean bC = (action != null) ? action.contains("C") : true;

						Map<Element, Integer> elements = new HashMap<Element, Integer>();

						if ((bM || bT) && e instanceof Resource) {
							for (Mapping<Task, Resource> mapping : mappings.get((Resource) e)) {
								Integer v = null;
								if (bM) {
									v = mapping.getAttribute(name);
								}
								if (bT && v == null) {
									v = mapping.getSource().getAttribute(name);
								}
								if (v != null && v != 0) {
									elements.put(mapping, v);
								}
							}
						}

						if (bC) {
							for (Task communication : filterCommunications(application)) {
								Integer v = communication.getAttribute(name);

								if (v != null && v != 0) {
									Architecture<Resource, Link> routing = routings.get(communication);

									boolean isIn = false;
									if (e instanceof Resource && routing.containsVertex((Resource) e)) {
										isIn = true;
									} else if (e instanceof Link && routing.containsEdge((Link) e)) {
										isIn = true;
									}

									if (isIn) {
										elements.put(communication, v);
									}
								}

							}
						}

						CapacityConstraint<?> capacityConstraint = null;

						if (e instanceof Resource) {
							capacityConstraint = new CapacityConstraint<Resource>((Resource) e, name, elements);
						} else { // e instanceof Link
							capacityConstraint = new CapacityConstraint<Link>((Link) e, name, elements);
						}
						capacityConstraints.add(capacityConstraint);
					}
				}

			}
		}
		return capacityConstraints;
	}

	private static class CapacityConstraint<E extends Element> {

		protected final E element;
		protected final String attribute;
		protected final Map<Element, Integer> elements;

		public CapacityConstraint(E element, String attribute, Map<Element, Integer> elements) {
			super();
			this.element = element;
			this.attribute = attribute;
			this.elements = elements;
		}

		public E getElement() {
			return element;
		}

		public String getAttribute() {
			return attribute;
		}

		public Map<Element, Integer> getElements() {
			return elements;
		}

	}

	protected static boolean isCapacity(String attributeName) {
		return attributeName.contains(CAPACITY);
	}

	public static String getName(String attributeName) {
		return attributeName.split(":")[0];
	}

	@Override
	public void doInterpreting(Specification implementation, Model model) {

		for (CapacityConstraint<?> capacityConstraint : getCapacityConstraints(implementation)) {
			Element element = capacityConstraint.getElement();
			String attribute = capacityConstraint.getAttribute();
			Map<Element, Integer> elements = capacityConstraint.getElements();

			ParameterObject cap = getParameter(element.getParent(), attribute + CAPACITY);
			ParameterObject min = getParameter(element.getParent(), attribute + CAPACITY_MIN);
			ParameterObject max = getParameter(element.getParent(), attribute + CAPACITY_MAX);

			// System.err.println(ViewUtil.getTooltip(element));

			if (!cap.isNull() && cap.isParameter()) {
				Set<Term> terms = getParameterTerms(element.getParent(), cap);
				for (Term term : terms) {
					if (model.get(term.getLiteral().variable())) {
						element.setAttribute(attribute + CAPACITY, term.getCoefficient());
					}
				}
			}
			if (!max.isNull() && max.isParameter()) {
				Set<Term> terms = getParameterTerms(element.getParent(), max);
				for (Term term : terms) {
					if (model.get(term.getLiteral().variable())) {
						element.setAttribute(attribute + CAPACITY_MAX, term.getCoefficient());
					}
				}
			}
			if (!min.isNull() && min.isParameter()) {
				Set<Term> terms = getParameterTerms(element.getParent(), min);
				for (Term term : terms) {
					if (model.get(term.getLiteral().variable())) {
						element.setAttribute(attribute + CAPACITY_MIN, term.getCoefficient());
					}
				}
			}

			int value = 0;
			for (int v : elements.values()) {
				value += v;
			}

			element.setAttribute(attribute + CAPACITY_VALUE, value);

			Integer vcap = element.getAttribute(attribute + CAPACITY);
			Integer vmin = element.getAttribute(attribute + CAPACITY_MIN);
			Integer vmax = element.getAttribute(attribute + CAPACITY_MAX);

			int c = 0;

			if (vcap != null) {
				c = vcap;
			} else if (vmax != null) {
				c = vmax;
			} else if (vmin != null) {
				c = vmin;
			}

			double ratio = (double) value / (double) c;

			element.setAttribute(attribute + CAPACITY_RATIO, ratio);

		}
	}



	@Deprecated
	protected Constraint scale(Constraint constraint, int scale) {
		if (constraint.getRhs() <= scale) {
			return constraint;
		}

		int rhs = constraint.getRhs();
		Operator op = constraint.getOperator();

		Constraint c = new Constraint(op, scale);

		double r = (double) scale / (double) rhs;

		for (Term term : constraint) {
			Literal lit = term.getLiteral();
			double coeff = term.getCoefficient() * r;

			switch (constraint.getOperator()) {
			case LE:
				coeff = Math.ceil(coeff);
				break;
			case GE:
				coeff = Math.floor(coeff);
				break;
			default:
				throw new IllegalArgumentException("cannot scale constraint " + constraint);
			}

			c.add((int) coeff, lit);
		}

		return c;
	}

}
