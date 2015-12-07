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

import org.opt4j.core.Genotype;
import org.opt4j.core.common.random.Rand;
import org.opt4j.core.genotype.Bounds;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.genotype.DoubleBounds;
import org.opt4j.core.genotype.DoubleMapGenotype;
import org.opt4j.core.genotype.IntegerBounds;
import org.opt4j.core.genotype.IntegerMapGenotype;
import org.opt4j.core.genotype.PermutationGenotype;
import org.opt4j.core.genotype.SelectMapGenotype;
import org.opt4j.core.problem.Creator;

import com.google.inject.Inject;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.model.parameter.ParameterRange;
import net.sf.opendse.model.parameter.ParameterRangeInt;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.model.parameter.ParameterSelect;
import net.sf.opendse.model.parameter.ParameterUniqueID;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;

public class ParameterCreator implements Creator<CompositeGenotype<String, Genotype>> {

	protected final SpecificationWrapper specWrapper;
	protected final Rand random;

	protected final Map<ParameterReference, Parameter> parameters = new TreeMap<ParameterReference, Parameter>();

	protected final List<ParameterReference> selectParameters = new ArrayList<ParameterReference>();
	protected final Map<ParameterReference, List<Object>> selectParametersMap = new HashMap<ParameterReference, List<Object>>();

	protected final List<ParameterReference> rangeParameters = new ArrayList<ParameterReference>();
	protected final Bounds<Double> rangeBounds;

	protected final List<ParameterReference> rangeParametersInt = new ArrayList<ParameterReference>();
	protected final Bounds<Integer> rangeBoundsInt;

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

		List<Integer> rangeLbInt = new ArrayList<Integer>();
		List<Integer> rangeUbInt = new ArrayList<Integer>();

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
				} else if (parameter instanceof ParameterRangeInt) {
					ParameterRangeInt parameterRange = (ParameterRangeInt) parameter;
					rangeParametersInt.add(ref);
					rangeLbInt.add(parameterRange.getLowerBound());
					rangeUbInt.add(parameterRange.getUpperBound());
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
		rangeBoundsInt = new IntegerBounds(rangeLbInt, rangeUbInt);
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
		if (!rangeParametersInt.isEmpty()) {
			IntegerMapGenotype<ParameterReference> range = new IntegerMapGenotype<ParameterReference>(
					rangeParametersInt, rangeBoundsInt);
			range.init(random);
			genotype.put("RANGEINT", range);
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
