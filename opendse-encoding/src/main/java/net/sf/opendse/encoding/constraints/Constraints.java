package net.sf.opendse.encoding.constraints;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;

/**
 * The {@link Constraints} provides methods for the convenient generation of
 * {@link Constraint}s that encode a certain relation between the input
 * {@link Variable}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class Constraints {

	private Constraints() {
	}

	/**
	 * Generates a set of {@link Constraint}s that encode an OR relationship between
	 * the {@link Variable}s provided as arguments and the variable provided as
	 * result. Example: arguments = (A, B, C); result = D; The generated constraints
	 * then encode the relation A or B or C = D.
	 * 
	 * @param arguments
	 *            the set of {@link Variable}s that shall affect the activation of
	 *            the result variable be their OR operation
	 * @param result
	 *            the {@link Variable} that shall be activated or deactivated in
	 *            accordance to the result of an OR operation of the argument
	 *            variables
	 * @return the set of {@link Constraint}s that encode an OR relationship between
	 *         the {@link Variable}s provided as arguments and the variable provided
	 *         as result
	 */
	public static Set<Constraint> generateOrConstraints(Set<Variable> arguments, Variable result) {
		Set<Constraint> orConstraints = new HashSet<Constraint>();
		for (Variable argument : arguments) {
			orConstraints.add(generatePositiveImplication(argument, result));
		}
		orConstraints.add(generateMinimalRequirementConstraint(arguments, result));
		return orConstraints;
	}

	/**
	 * Generates the {@link Constraint} stating that the result {@link Variable} can
	 * only be active if at least one of the condition {@link Variable}s is active.
	 * Example: conditions = (A,B); result = C; A = 0, B = 0 => C = 0
	 * 
	 * @param conditions
	 *            the result {@link Variable} can only be activated if at least one
	 *            of these {@link Variable}s is active
	 * @param result
	 *            the {@link Variable} that shall be deactivated if all condition
	 *            {@link Variable}s are deactivated
	 * @return the {@link Constraint} stating that the result {@link Variable} can
	 *         only be active if at least one of the condition {@link Variable}s is
	 *         active
	 */
	public static Constraint generateMinimalRequirementConstraint(Set<Variable> conditions, Variable result) {
		Constraint minimalRequirementConstraint = new Constraint(Operator.LE, 0);
		minimalRequirementConstraint.add(Variables.p(result));
		for (Variable condition : conditions) {
			minimalRequirementConstraint.add(-1, Variables.p(condition));
		}
		return minimalRequirementConstraint;
	}

	/**
	 * Generates the {@link Constraint} stating that the
	 * implication-{@link Variable} is always active if the
	 * condition-{@link Variable} is active.
	 * 
	 * @param condition
	 *            the {@link Variable} whose activation serves as condition
	 * @param implication
	 *            the {@link Variable} which as always activated if the condition is
	 *            fulfilled
	 * @return the {@link Constraint} stating that the implication-{@link Variable}
	 *         is always active if the condition-{@link Variable} is active
	 */
	public static Constraint generatePositiveImplication(Variable condition, Variable implication) {
		Constraint positiveImplication = new Constraint(Operator.LE, 0);
		positiveImplication.add(Variables.p(condition));
		positiveImplication.add(-1, Variables.p(implication));
		return positiveImplication;
	}

}
