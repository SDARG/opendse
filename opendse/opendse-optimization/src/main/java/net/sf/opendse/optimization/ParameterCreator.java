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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.model.parameter.ParameterRange;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;
import net.sf.opendse.model.parameter.ParameterUniqueID;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;

import org.opt4j.core.Genotype;
import org.opt4j.core.common.random.Rand;
import org.opt4j.core.genotype.Bounds;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.genotype.PermutationGenotype;
import org.opt4j.core.genotype.SelectMapGenotype;
import org.opt4j.core.problem.Creator;

import com.google.inject.Inject;

public class ParameterCreator implements Creator<CompositeGenotype<String, Genotype>> {

	protected final SpecificationWrapper specWrapper;
	protected final Rand random;

	protected final Map<ParameterReference, Parameter> parameters = new TreeMap<ParameterReference, Parameter>();

	protected final List<ParameterReference> selectParameters = new ArrayList<ParameterReference>();
	protected final Map<ParameterReference, List<Object>> selectParametersMap = new HashMap<ParameterReference, List<Object>>();

	protected final List<ParameterReference> rangeParameters = new ArrayList<ParameterReference>();
	protected final Bounds<Double> rangeBounds;

	protected final List<String> uniqueIDs = new ArrayList<String>();
	protected final Map<String, List<ParameterReference>> uniqueIDMap = new HashMap<String, List<ParameterReference>>();

	protected final Set<ParameterReference> activeParameters = new HashSet<ParameterReference>();

	@Inject
	public ParameterCreator(SpecificationWrapper specWrapper, Rand random,
			SpecificationConstraints specificationConstraints) {
		super();
		this.specWrapper = specWrapper;
		this.random = random;
		this.activeParameters.addAll(specificationConstraints.getActiveParameters());

		Specification specification = specWrapper.getSpecification();

		Set<Element> elements = Models.getElements(specification);

		for (Element element : elements) {
			for (String name : element.getAttributeNames()) {
				Parameter parameter = element.getAttributeParameter(name);
				if (parameter != null) {
					ParameterReference ref = new ParameterReference(element, name);
					parameters.put(ref, parameter);
				}
			}
		}

		List<Double> rangeLb = new ArrayList<Double>();
		List<Double> rangeUb = new ArrayList<Double>();

		for (Entry<ParameterReference, Parameter> entry : parameters.entrySet()) {
			ParameterReference ref = entry.getKey();
			Parameter parameter = entry.getValue();

			if (!activeParameters.contains(ref)) {

				if (parameter instanceof ParameterSelect) {
					ParameterSelect parameterSelect = (ParameterSelect) parameter;
					if (parameterSelect.getReference() == null) {
						selectParameters.add(ref);
						selectParametersMap.put(ref, Arrays.asList(parameterSelect.getElements()));
					}
				} else if (parameter instanceof ParameterRange) {
					ParameterRange parameterRange = (ParameterRange) parameter;
					rangeParameters.add(ref);
					rangeLb.add(parameterRange.getLowerBound());
					rangeUb.add(parameterRange.getUpperBound());
				} else if (parameter instanceof ParameterUniqueID) {
					ParameterUniqueID parameterUniqueID = (ParameterUniqueID) parameter;
					String id = parameterUniqueID.getIdentifier();

					if (!uniqueIDs.contains(id)) {
						uniqueIDs.add(id);
						uniqueIDMap.put(id, new ArrayList<ParameterReference>());
					}
					uniqueIDMap.get(id).add(ref);
				}

			}
		}

		rangeBounds = new DoubleBounds(rangeLb, rangeUb);
	}

	@Override
	public CompositeGenotype<String, Genotype> create() {
		CompositeGenotype<String, Genotype> genotype = new CompositeGenotype<String, Genotype>();

		if (!selectParameters.isEmpty()) {
			SelectMapGenotype<ParameterReference, Object> select = new SelectMapGenotype<ParameterReference, Object>(
					selectParameters, selectParametersMap);
			select.init(random);
			genotype.put("SELECT", select);
		}
		if (!rangeParameters.isEmpty()) {
			DoubleMapGenotype<ParameterReference> range = new DoubleMapGenotype<ParameterReference>(rangeParameters,
					rangeBounds);
			range.init(random);
			genotype.put("RANGE", range);
		}
		for (String uniqueID : uniqueIDs) {
			PermutationGenotype<ParameterReference> permutation = new PermutationGenotype<ParameterReference>();
			permutation.addAll(uniqueIDMap.get(uniqueID));
			Collections.shuffle(permutation, random);
			genotype.put("UID_" + uniqueID, permutation);
		}

		return genotype;
	}
}
