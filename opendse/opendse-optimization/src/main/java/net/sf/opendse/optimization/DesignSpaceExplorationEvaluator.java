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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sf.opendse.model.Specification;

import org.opt4j.core.DoubleValue;
import org.opt4j.core.IntegerValue;
import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.problem.Evaluator;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class DesignSpaceExplorationEvaluator implements Evaluator<ImplementationWrapper> {

	protected final List<ImplementationEvaluator> evaluators;
	protected final Provider<Objectives> objectivesProvider;

	@Inject
	public DesignSpaceExplorationEvaluator(Set<ImplementationEvaluator> evaluators, Provider<Objectives> objectivesProvider) {
		super();
		this.evaluators = new ArrayList<ImplementationEvaluator>(evaluators);
		this.objectivesProvider = objectivesProvider;
		Collections.sort(this.evaluators, new Comparator<ImplementationEvaluator>() {
			@Override
			public int compare(ImplementationEvaluator o1, ImplementationEvaluator o2) {
				Integer i1 = o1.getPriority();
				Integer i2 = o2.getPriority();
				return i1.compareTo(i2);
			}
		});
	}

	@Override
	public Objectives evaluate(ImplementationWrapper wrapper) {

		Objectives objectives = objectivesProvider.get();
		for (ImplementationEvaluator evaluator : evaluators) {
			Specification impl = evaluator.evaluate(wrapper.getImplementation(), objectives);
			if (impl != null) {
				wrapper.setImplementation(impl);
			}
			for (Objective objective : objectives.getKeys()) {

				String attribute = objective.getName() + ":OBJECTIVE";
				Object value = objectives.get(objective);
				if (value instanceof DoubleValue) {
					value = ((DoubleValue) value).getValue();
				} else if (value instanceof IntegerValue) {
					value = ((IntegerValue) value).getValue();
				} else {
					value = value.toString();
				}

				Specification implementation = wrapper.getImplementation();
				if (implementation != null) {
					implementation.setAttribute(attribute, value);
				}
			}

		}
		return objectives;
	}

}
