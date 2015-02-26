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
package net.sf.opendse.optimization.encoding.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.DefaultSolver;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Model;
import org.opt4j.satdecoding.TimeoutException;
import org.opt4j.satdecoding.VarOrder;
import org.opt4j.satdecoding.sat4j.SAT4JSolver;

/**
 * The {@code Solving} returns a {@code Model} for a set of constraints.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Solving {

	Random random = new Random();

	public Model solve(Collection<Constraint> constraints) {
		SAT4JSolver solver = new DefaultSolver();

		Set<Object> variables = new HashSet<Object>();
		for (Constraint constraint : constraints) {
			solver.addConstraint(constraint);
			for (Literal literal : constraint.getLiterals()) {
				variables.add(literal.variable());
			}
		}

		VarOrder order = new VarOrder();
		for (Object variable : variables) {
			boolean b = random.nextBoolean();
			order.setPhase(variable, b);
		}
		
		Model model = null;
		try {
			model = solver.solve(order);
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return model;
	}

}
