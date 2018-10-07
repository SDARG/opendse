package net.sf.opendse.optimization.constraints;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;

import net.sf.opendse.encoding.firm.variables.Variables;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;

public class SpecificationConstraintInterpreter {

	protected final SpecificationConstraints specificationConstraints;
	protected final Set<ParameterReference> activeVariables;

	@Inject
	public SpecificationConstraintInterpreter(SpecificationConstraints specificationConstraints) {
		this.specificationConstraints = specificationConstraints;
		this.activeVariables = new HashSet<ParameterReference>(specificationConstraints.getActiveParameters());
	}

	public void interpretSpecificationConstraints(Specification implementation, Model model) {
		Map<String, Element> map = Models.getElementsMap(implementation);
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
		}
		specificationConstraints.doInterpreting(implementation, model);
	}
}
