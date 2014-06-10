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
