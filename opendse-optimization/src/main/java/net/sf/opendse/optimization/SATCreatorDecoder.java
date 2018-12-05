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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.opendse.encoding.ImplementationInterpreter;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.constraints.SpecificationConstraintInterpreter;

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

	final VariableClassOrder order;
	Map<Class<?>, Double> lb = new HashMap<Class<?>, Double>();
	Map<Class<?>, Double> ub = new HashMap<Class<?>, Double>();

	protected final SATConstraints constraints;
	protected final SpecificationWrapper specificationWrapper;
	protected final ImplementationInterpreter interpreter;
	protected final SpecificationConstraintInterpreter specificationConstraintInterpreter;
	protected final Control control;


	@Inject
	public SATCreatorDecoder(VariableClassOrder order, SATManager manager, Rand random, SATConstraints constraints, SpecificationWrapper specificationWrapper,
			ImplementationInterpreter interpreter, Control control,
			@Constant(value = "variableorder", namespace = SATCreatorDecoder.class) boolean useVariableOrder, SpecificationConstraintInterpreter specificationConstraintInterpreter) {
		super(manager, random);
		this.order = order;
		this.constraints = constraints;
		this.specificationWrapper = specificationWrapper;
		this.interpreter = interpreter;
		this.control = control;
		this.specificationConstraintInterpreter = specificationConstraintInterpreter;
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
		specificationConstraintInterpreter.interpretSpecificationConstraints(implementation, model);
		ImplementationWrapper wrapper = new ImplementationWrapper(implementation);
		return wrapper;
	}

	@Override
	public Set<Constraint> createConstraints() {
		Set<Constraint> constraints = new HashSet<Constraint>(this.constraints.getConstraints());
		return constraints;
	}

	@Override
	public void randomize(Collection<Object> variables, Map<Object, Double> lowerBounds,
			Map<Object, Double> upperBounds, Map<Object, Double> priorities, Map<Object, Boolean> phases) {
		// Iterates the encoding variables. Without preprocessing, these are the
		// variables used for the formulation of the SAT constraints. With
		// activated constraint preprocessing, the set of variables iterated
		// here is potentially smaller and may consists of simplified variables.
		for (Object variable : variables) {
			// Sets the phases of the SAT-chromosomes during the randomized
			// individual creation. If the phase is set to true, the
			// corresponding variable will first be assigned to 1 during the
			// constraint resolution by the SAT solver.
			if (variable instanceof Resource) {
				// The phase of a resource variable is initially set to 1 in 20
				// % of all cases
				phases.put(variable, random.nextDouble() < 0.2);
			} else {
				// All other variables are set randomly, i.e., they are set to 1
				// in 50 % of all cases
				phases.put(variable, random.nextDouble() < 0.5);
			}
			// The order genes of the SAT-genotypes correspond to the activity
			// that the SAT-solver assigns to the variables during the
			// constraint resolution. Variables with a HIGHER activity are set
			// FIRST. Through application of the evolutionary operators during
			// the optimization, the
			// activity of each variable is varied between its lower and its
			// upper bound. Yet, in this implementation, the different variable
			// classes
			// specified in the order-list are assigned to non-overlapping
			// activity intervals. So, e.g., while the relative activity of
			// different Resource-variables can change throughout the
			// exploration, the assignment of all Resource-variables is ALWAYS
			// done before (after) the assignment of all Link-variables if the
			// Resource.class has a lower (higher) index in the order-list than
			// the Link.class.

			// The activity space between 0.0 and 1.0 is divided into n
			// intervals of equal size, with n being the size of the order list.
			// Each variable class is assigned to the interval corresponding to
			// its position in the list.
			// Example: With 5 variable classes in the order-list, the variable
			// class at list position 1 is assigned the activity interval
			// between
			// .2 and .4
			double lbv = getLowerOrderBound(order.getOrderSize(), order.indexOf(variable));
			double ubv = getUpperOrderBound(order.getOrderSize(), order.indexOf(variable));
			double prio = lbv + random.nextDouble() * (ubv - lbv);
			priorities.put(variable, prio);
		}
	}

	/**
	 * Returns the upper bound for the order of the class on the given position of
	 * the order list.
	 * 
	 * @param orderSize  the size of the order list, that is, the number of classes
	 *                   (not object instances!) that are included in the SAT
	 *                   genotype
	 * @param orderIndex the position of the variable's class in the order list
	 * @return the upper bound for the order of the class on the given position of
	 *         the order list
	 */
	protected double getUpperOrderBound(int orderSize, int orderIndex) {
		if (orderIndex == -1) {
			throw new IllegalArgumentException("Illegal order index provided.");
		}
		return (orderSize - orderIndex) * 1.0 / orderSize;
	}

	/**
	 * Returns the lower bound for the order of the class on the given position of
	 * the order list.
	 * 
	 * @param orderSize  the size of the order list, that is, the number of classes
	 *                   (not object instances!) that are included in the SAT
	 *                   genotype
	 * @param orderIndex the position of the variable's class in the order list
	 * @return the lower bound for the order of the class on the given position of
	 *         the order list
	 */
	protected double getLowerOrderBound(int orderSize, int orderIndex) {
		if (orderIndex == -1) {
			throw new IllegalArgumentException("Illegal order index provided.");
		}
		return (orderSize - orderIndex - 1) * 1.0 / orderSize;
	}

	@Override
	public Set<Object> ignoreVariables(Set<Object> variables) {
		// All variable classes that are not in the order list (the
		// indexof()-method of the order list returns -1 in these cases) are
		// ignored.
		// While they remain part of the constraint system and are set during
		// the constraint resolution, they are not part of the SAT-genotype and
		// therefore: 1) Do not contribute to the growth of the search space and
		// 2) can not be used by the optimizer to alter individuals.
		Set<Object> ignore = super.ignoreVariables(variables);
		for (Object object : variables) {
			int index = order.indexOf(object);
			if (index == -1) {
				ignore.add(object);
			}
		}
		return ignore;
	}

	@Override
	public Map<Object, Double> getLowerBounds(Set<Object> variables) {
		Map<Object, Double> map = super.getLowerBounds(variables);
		return map;
	}

	@Override
	public Map<Object, Double> getUpperBounds(Set<Object> variables) {
		Map<Object, Double> map = this.getLowerBounds(variables);
		return map;
	}

}
