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
package net.sf.opendse.optimization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.problem.Decoder;

import com.google.inject.Inject;

public class DesignSpaceExplorationDecoder implements
		Decoder<CompositeGenotype<String, Genotype>, ImplementationWrapper> {

	protected final SATCreatorDecoder satDecoder;
	protected final ParameterDecoder parameterDecoder;

	protected final Map<ParameterReference, ParameterReference> selectParameterRef = new HashMap<ParameterReference, ParameterReference>();
	protected final Map<ParameterReference, List<Object>> selectParametersMap = new HashMap<ParameterReference, List<Object>>();

	@Inject
	public DesignSpaceExplorationDecoder(SATCreatorDecoder satDecoder, ParameterDecoder parameterDecoder,
			SpecificationWrapper specWrapper) {
		super();
		this.satDecoder = satDecoder;
		this.parameterDecoder = parameterDecoder;

		for (Element element : Models.getElements(specWrapper.getSpecification())) {
			for (String name : element.getAttributeNames()) {
				Parameter parameter = element.getAttributeParameter(name);
				if (parameter != null && parameter instanceof ParameterSelect) {
					ParameterSelect parameterSelect = (ParameterSelect) parameter;

					String reference = parameterSelect.getReference();

					if (reference != null) {
						ParameterReference pref = new ParameterReference(element, name);
						ParameterReference prefref = new ParameterReference(element, reference);

						selectParameterRef.put(pref, prefref);
						selectParametersMap.put(pref, Arrays.asList(parameterSelect.getElements()));
					}
				}
			}
		}
	}

	@Override
	public ImplementationWrapper decode(CompositeGenotype<String, Genotype> genotype) {
		Genotype satGenotype = genotype.get("SAT");
		CompositeGenotype<String, Genotype> parameterGenotype = genotype.get("PARAMETER");

		ImplementationWrapper wrapper = satDecoder.decode(satGenotype);
		ParameterMap parameterMap = parameterDecoder.decode(parameterGenotype);

		Specification implementation = wrapper.getImplementation();

		if (implementation != null) {
			Map<String, Element> elementMap = Models.getElementsMap(implementation);

			if (!parameterMap.isEmpty()) {
				for (Entry<ParameterReference, Object> entry : parameterMap.entrySet()) {
					ParameterReference ref = entry.getKey();
					String id = ref.getId();
					String attribute = ref.getAttribute();

					if (elementMap.containsKey(id)) {
						Element element = elementMap.get(id);
						Object value = entry.getValue();
						element.setAttribute(attribute, value);
					}
				}
			}

			for (Entry<ParameterReference, ParameterReference> entry : selectParameterRef.entrySet()) {
				ParameterReference ref = entry.getKey();
				ParameterReference refref = entry.getValue();

				String id = ref.getId();
				String refAttribute = ref.getAttribute();
				String refrefAttribute = refref.getAttribute();

				Element element = elementMap.get(id);
				if (element != null) {
					Object o = element.getAttribute(refrefAttribute);
					int index = ((ParameterSelect) (refref.getParameter())).indexOf(o);

					Object o2 = selectParametersMap.get(ref).get(index);
					element.setAttribute(refAttribute, o2);
				}
			}
		}

		return wrapper;
	}
}
