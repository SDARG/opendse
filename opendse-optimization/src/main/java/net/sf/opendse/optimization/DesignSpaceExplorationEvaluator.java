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
