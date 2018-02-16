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
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.ContradictionException;
import org.opt4j.satdecoding.DefaultSolver;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Model;
import org.opt4j.satdecoding.Solver;
import org.opt4j.satdecoding.TimeoutException;
import org.opt4j.satdecoding.VarOrder;

/**
 * This class performs a binary search on a linear search problem, i.e., it
 * search if some variables have to be 0 or 1 to fulfill all constraints.
 * 
 */
public class BinaryReachability {

	protected final Solver solver = new DefaultSolver(); 
	
	public Set<Literal> search(Set<Constraint> constraints, Set<Literal> literals) {
		
		Collection<Literal> candidates = new HashSet<Literal>(literals);
		
		try {
			for (Constraint constraint : constraints) {
				solver.addConstraint(constraint);
			}

			while (candidates.size() > 0) {
				Constraint constraint = new Constraint(">=", 1);

				for (Literal literal : candidates) {
					constraint.add(literal);
				}
				solver.addConstraint(constraint);

				Model model = solve();
				

				if (model == null) {
					break;
				}

				Set<Literal> remove = new HashSet<Literal>();
				for (Literal candidate : candidates) {
					Object var = candidate.variable();
					boolean phase = candidate.phase();

					if (model.get(var) != null && model.get(var) == phase) {
						remove.add(candidate);
					}
				}
				candidates.removeAll(remove);
			}

		} catch (ContradictionException e) {

		}

		Set<Literal> lits = new HashSet<Literal>();
		for(Literal candidate: candidates){
			lits.add(candidate.negate());
		}

		return lits;
	}

	protected Model solve() {

		try {
			return solver.solve(new VarOrder());
		} catch (TimeoutException e) {
			System.err.println("Timeout in preprocessing: " + this.getClass());
		}

		return null;
	}

}
