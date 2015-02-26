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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.encoding.Interpreter;
import net.sf.opendse.optimization.encoding.variables.CR;
import net.sf.opendse.optimization.encoding.variables.EAVI;

import org.opt4j.core.Genotype;
import org.opt4j.core.common.random.Rand;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.start.Constant;
import org.opt4j.satdecoding.AbstractSATDecoder;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;
import org.opt4j.satdecoding.SATManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SATCreatorDecoder extends AbstractSATDecoder<Genotype, ImplementationWrapper> {

	List<Class<?>> order = new ArrayList<Class<?>>();
	Map<Class<?>, Double> lb = new HashMap<Class<?>, Double>();
	Map<Class<?>, Double> ub = new HashMap<Class<?>, Double>();

	int indexOf(Object object) {
		for (int i = 0; i < order.size(); i++) {
			if (order.get(i).isAssignableFrom(object.getClass())) {
				return i;
			}
		}
		return -1;
	}

	protected final SATConstraints constraints;
	protected final SpecificationWrapper specificationWrapper;
	protected final Interpreter interpreter;
	protected final Control control;
	protected final boolean useVariableOrder;

	@Inject
	public SATCreatorDecoder(SATManager manager, Rand random, SATConstraints constraints, SpecificationWrapper specificationWrapper,
			Interpreter interpreter, Control control,
			@Constant(value = "variableorder", namespace = SATCreatorDecoder.class) boolean useVariableOrder) {
		super(manager, random);
		this.constraints = constraints;
		this.specificationWrapper = specificationWrapper;
		this.interpreter = interpreter;
		this.control = control;
		this.useVariableOrder = useVariableOrder;

		if (useVariableOrder) {
			order.add(Resource.class);
			order.add(Link.class);
			order.add(EAVI.class);
			order.add(Mapping.class);
			order.add(CR.class);
		} else {
			order.add(Object.class);
		}
	}

	@Override
	public ImplementationWrapper convertModel(Model model) {
		if (model == null) {
			control.doTerminate();
			System.err.println("No feasible implementation exists.");
			return new ImplementationWrapper(null);
		}

		model = constraints.decorate(model);

		Specification specification = specificationWrapper.getSpecification();
		Specification implementation = interpreter.toImplementation(specification, model);
		ImplementationWrapper wrapper = new ImplementationWrapper(implementation);

		return wrapper;
	}

	@Override
	public Set<Constraint> createConstraints() {
		Set<Constraint> constraints = new HashSet<Constraint>(this.constraints.getConstraints());
		return constraints;
	}

	@Override
	public void randomize(Collection<Object> variables, Map<Object, Double> lowerBounds, Map<Object, Double> upperBounds,
			Map<Object, Double> priorities, Map<Object, Boolean> phases) {

		for (Object variable : variables) {

			if (variable instanceof Resource) {
				phases.put(variable, random.nextDouble() < 0.2);
			} else {
				phases.put(variable, random.nextDouble() < 0.5);
			}

			Class<?> clazz = variable.getClass();

			double lbv = ((double) indexOf(clazz)) / order.size();
			double ubv = ((double) (1 + indexOf(clazz))) / order.size();

			double prio = lbv + random.nextDouble() * (ubv - lbv);

			// System.out.println(variable+" "+prio);
			priorities.put(variable, prio);
		}

	}

	@Override
	public Set<Object> ignoreVariables(Set<Object> variables) {
		Set<Object> ignore = super.ignoreVariables(variables);
		for (Object object : variables) {
			int index = indexOf(object);

			if (index == -1) {
				ignore.add(object);
			}
		}
		return ignore;
	}

	@Override
	public Map<Object, Double> getLowerBounds(Set<Object> variables) {
		Map<Object, Double> map = super.getLowerBounds(variables);

		/*
		 * for (Object var : variables) { int index = indexOf(var); assert
		 * (index != -1);
		 * 
		 * double lower = order.size() - index - 1; map.put(var, lower); }
		 */

		return map;
	}

	@Override
	public Map<Object, Double> getUpperBounds(Set<Object> variables) {
		Map<Object, Double> map = this.getLowerBounds(variables);

		/*
		 * for (Object var : variables) { double value = map.get(var) + 1;
		 * map.put(var, value); }
		 */

		return map;
	}

}
