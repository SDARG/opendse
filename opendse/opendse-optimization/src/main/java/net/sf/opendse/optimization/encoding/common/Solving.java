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
