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
package net.sf.opendse.encoding.specificationconstraints;

import static net.sf.opendse.encoding.firm.variables.Variables.p;
import static net.sf.opendse.encoding.firm.variables.Variables.var;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Term;

public abstract class AbstractSpecificationConstraints implements SpecificationConstraints {

	protected final Set<Constraint> constraints = new HashSet<Constraint>();
	protected final Set<ParameterReference> activeParameters = new HashSet<ParameterReference>();

	@Override
	public Set<ParameterReference> getActiveParameters() {
		return activeParameters;
	}

	@Override
	public void doEncoding(Collection<Constraint> constraints) {
		constraints.addAll(this.constraints);
	}
	
	public static class ParameterObject {

		protected final Object object;
		protected final String attribute;

		public ParameterObject(Object object, String attribute) {
			super();
			this.object = object;
			this.attribute = attribute;
		}

		public boolean isParameter() {
			return object instanceof ParameterSelect;
		}

		public boolean isNull() {
			return object == null;
		}

		public int getInteger() {
			return (Integer) object;
		}

		public ParameterSelect getParameter() {
			return (ParameterSelect) object;
		}

		public String getAttribute() {
			return attribute;
		}

	}

	public ParameterObject getParameter(Element element, String attribute) {
		Object object = element.getAttributeParameter(attribute);
		if (object == null) {
			object = element.getAttribute(attribute);
		}
		return new ParameterObject(object, attribute);
	}

	public Set<Term> getParameterTerms(Element element, ParameterObject parameterObject) {
		Set<Term> terms = new HashSet<Term>();

		ParameterSelect parameter = parameterObject.getParameter();
		String attribute = parameter.getReference() == null ? parameterObject.getAttribute() : parameter.getReference();
		ParameterSelect ref = (ParameterSelect) element.getAttributeParameter(attribute);

		// TODO check when writing unit tests
		// assert (ref != null) : element + " " + attribute + " " + ViewUtil.getTooltip(element);
		// assert (parameter.getElements().length == ref.getElements().length) : parameter + " " + ref;
		// assert (ref.getReference() == null);

		activeParameters.add(new ParameterReference(element, attribute));

		for (int i = 0; i < parameter.getElements().length; i++) {
			Integer coeff = (Integer) parameter.getElements()[i];
			Object v = ref.getElements()[i];

			Term term = new Term(coeff, p(var(element, attribute, v, i)));
			terms.add(term);
		}

		return terms;
	}

}
