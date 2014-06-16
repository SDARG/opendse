/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.optimization.constraints;

import static net.sf.opendse.optimization.encoding.variables.Variables.p;
import static net.sf.opendse.optimization.encoding.variables.Variables.var;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;
import net.sf.opendse.visualization.ViewUtil;

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

		assert (ref != null) : element + " " + attribute + " " + ViewUtil.getTooltip(element);
		assert (parameter.getElements().length == ref.getElements().length) : parameter + " " + ref;
		assert (ref.getReference() == null);

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
