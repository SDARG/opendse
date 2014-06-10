package net.sf.opendse.encoding.common;

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
