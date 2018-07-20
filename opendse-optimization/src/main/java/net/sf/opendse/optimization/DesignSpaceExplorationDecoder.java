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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.optimization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.SpecificationWrapper;
import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.problem.Decoder;
import org.opt4j.satdecoding.ContradictionException;

import com.google.inject.Inject;

public class DesignSpaceExplorationDecoder implements
		Decoder<CompositeGenotype<String, Genotype>, ImplementationWrapper> {

	protected final SATCreatorDecoder satDecoder;
	protected final ParameterDecoder parameterDecoder;
	protected final Specification specification;

	protected final Map<ParameterReference, ParameterReference> selectParameterRef = new HashMap<ParameterReference, ParameterReference>();
	protected final Map<ParameterReference, List<Object>> selectParametersMap = new HashMap<ParameterReference, List<Object>>();

	@Inject
	public DesignSpaceExplorationDecoder(SATCreatorDecoder satDecoder, ParameterDecoder parameterDecoder,
			SpecificationWrapper specWrapper) {
		super();
		this.satDecoder = satDecoder;
		this.parameterDecoder = parameterDecoder;
		this.specification = specWrapper.getSpecification();

		for (Element element : Models.getElements(specification)) {
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

		ImplementationWrapper wrapper = null;
		try {
			wrapper = satDecoder.decode(satGenotype);
		} catch (ContradictionException e) {
			System.err.println("Stopping");
			throw e;
		}

		Specification implementation = wrapper.getImplementation();

		if (implementation != null) {
			decodeParameters(parameterGenotype, implementation);
		}

		return wrapper;
	}

	/**
	 * Decodes the {@link Parameter}s and sets the according {@link Attributes}.
	 * 
	 * @param parameterGenotype
	 *            the parameter genotype
	 * @param implementation
	 *            the corresponding implementation to augment
	 */
	private void decodeParameters(CompositeGenotype<String, Genotype> parameterGenotype, Specification implementation) {
		ParameterMap parameterMap = parameterDecoder.decode(parameterGenotype);

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
}
