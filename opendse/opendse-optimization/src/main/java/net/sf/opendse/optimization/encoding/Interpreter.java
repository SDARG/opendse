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
package net.sf.opendse.optimization.encoding;

import static edu.uci.ics.jung.graph.util.EdgeType.DIRECTED;
import static net.sf.opendse.model.Models.filterCommunications;
import static net.sf.opendse.model.Models.getLinks;
import static net.sf.opendse.optimization.encoding.variables.Variables.var;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Function;
import net.sf.opendse.model.IAttributes;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;
import net.sf.opendse.optimization.encoding.variables.Variables;

import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * The {@code Interpreter} receives a {@code Model} that satisfies the
 * constraints and determines the corresponding {@code Specification}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Interpreter {

	@SuppressWarnings("unchecked")
	public <E extends Element> E copy(Element element) {
		try {
			Constructor<? extends Element> cstr = element.getClass().getConstructor(Element.class);
			Element copy = cstr.newInstance(element);
			return (E) copy;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <M extends Mapping<?, ?>> M copy(Mapping<?, ?> mapping) {
		try {
			Constructor<? extends Element> cstr = mapping.getClass().getConstructor(Element.class, Task.class, Resource.class);
			Element copy = cstr.newInstance(mapping, mapping.getSource(), mapping.getTarget());
			return (M) copy;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected final SpecificationConstraints specificationConstraints;
	protected final Set<ParameterReference> activeVariables;

	@Inject
	public Interpreter(SpecificationConstraints specificationConstraints) {
		super();
		this.specificationConstraints = specificationConstraints;
		this.activeVariables = new HashSet<ParameterReference>(specificationConstraints.getActiveParameters());
	}

	public Specification toImplementation(Specification specification, Model model) {

		// Boolean TRUE = new Boolean(true);
		// Boolean FALSE = new Boolean(false);

		Architecture<Resource, Link> sArchitecture = specification.getArchitecture();
		Application<Task, Dependency> sApplication = specification.getApplication();
		Mappings<Task, Resource> sMappings = specification.getMappings();
		Routings<Task, Resource, Link> sRoutings = specification.getRoutings();

		Architecture<Resource, Link> iArchitecture = new Architecture<Resource, Link>();
		Application<Task, Dependency> iApplication = new Application<Task, Dependency>();
		Mappings<Task, Resource> iMappings = new Mappings<Task, Resource>();
		Routings<Task, Resource, Link> iRoutings = new Routings<Task, Resource, Link>();

		for (Resource r : sArchitecture) {
			if (model.get(r)) {
				iArchitecture.addVertex((Resource) copy(r));
			}
		}
		for (Link l : sArchitecture.getEdges()) {
			if (model.get(l)) {
				Pair<Resource> endpoints = sArchitecture.getEndpoints(l);
				Resource source = iArchitecture.getVertex(endpoints.getFirst());
				Resource dest = iArchitecture.getVertex(endpoints.getSecond());
				iArchitecture.addEdge((Link) copy(l), source, dest, sArchitecture.getEdgeType(l));
			}
		}

		// copy application (including function attributes)
		for (Task t : sApplication) {
			iApplication.addVertex((Task) copy(t));
		}
		for (Dependency e : sApplication.getEdges()) {
			Pair<Task> endpoints = sApplication.getEndpoints(e);
			Task source = iApplication.getVertex(endpoints.getFirst());
			Task dest = iApplication.getVertex(sApplication.getVertex(endpoints.getSecond()));
			iApplication.addEdge((Dependency) copy(e), source, dest, sApplication.getEdgeType(e));
		}

		for (Function<Task, Dependency> function : iApplication.getFunctions()) {
			Task t = function.iterator().next();
			setAttributes(function, sApplication.getFunction(t).getAttributes());
		}

		for (Mapping<Task, Resource> m : sMappings) {
			if (model.get(m)) {
				Mapping<Task, Resource> copy = copy(m);
				copy.setSource(iApplication.getVertex(m.getSource()));
				copy.setTarget(iArchitecture.getVertex(m.getTarget()));
				iMappings.add(copy);
			}
		}

		for (Task c : filterCommunications(sApplication)) {
			Architecture<Resource, Link> sRouting = sRoutings.get(c);
			Architecture<Resource, Link> iRouting = new Architecture<Resource, Link>();

			for (Resource r : sRouting) {
				if (model.get(var(c, r))) {
					r = iArchitecture.getVertex(r);
					iRouting.addVertex((Resource) copy(r));
				}
			}
			for (DirectedLink lrr : getLinks(sRouting)) {
				if (model.get(var(c, lrr))) {
					Link l = iArchitecture.getEdge(lrr.getLink());
					Resource r0 = iRouting.getVertex(lrr.getSource());
					Resource r1 = iRouting.getVertex(lrr.getDest());
					iRouting.addEdge((Link) copy(l), r0, r1, DIRECTED);
				}
				// System.out.println(c+" "+lrr.getLink()+" "+lrr.getSource()+" "+lrr.getDest()+" "+model.get(var(c,
				// lrr)));
			}

			// System.out.println(sRouting+" "+iRouting);

			WeakComponentClusterer<Resource, Link> clusterer = new WeakComponentClusterer<Resource, Link>();
			Set<Set<Resource>> cluster = clusterer.transform(iRouting);

			Task sender = iApplication.getPredecessors(c).iterator().next();

			Set<Resource> targets = iMappings.getTargets(sender);

			for (Set<Resource> set : cluster) {
				boolean containsAny = false;
				for (Resource target : targets) {
					if (set.contains(target)) {
						containsAny = true;
						break;
					}
				}
				if (!containsAny) {
					for (Resource r : set) {
						iRouting.removeVertex(r);
					}
				}
			}

			iRoutings.set(iApplication.getVertex(c), iRouting);
		}

		Specification impl = new Specification(iApplication, iArchitecture, iMappings, iRoutings);

		Map<String, Element> map = Models.getElementsMap(impl);

		// set active parameters
		for (ParameterReference paramRef : activeVariables) {
			String id = paramRef.getId();
			String attribute = paramRef.getAttribute();

			Element element = map.get(id);

			if (element != null) {
				ParameterSelect parameter = (ParameterSelect) element.getAttributeParameter(attribute);

				for (int i = 0; i < parameter.getElements().length; i++) {
					Object v = parameter.getElements()[i];
					Boolean b = model.get(Variables.var(element, attribute, v, i));
					if (b) {
						element.setAttribute(attribute, v);
					}
				}

			}

			// System.out.println(paramRef);
		}

		specificationConstraints.doInterpreting(impl, model);

		return impl;
	}

	protected static void setAttributes(IAttributes e, Attributes attributes) {
		for (String name : attributes.keySet()) {
			e.setAttribute(name, attributes.get(name));
		}
	}

}
