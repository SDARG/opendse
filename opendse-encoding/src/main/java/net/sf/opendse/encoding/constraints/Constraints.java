package net.sf.opendse.encoding.constraints;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;

import net.sf.opendse.encoding.variables.AndVariable;
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
	 * Returns a variable that encodes the AND relation of the given arguments. The
	 * variables is encoded by adding additional constraints to the provided set of
	 * constraints.
	 * 
	 * @param arguments
	 *            the {@link Variable}s whose AND-relation is encoded
	 * @param constraints
	 *            the set of prevailing constraints
	 * @return a variable that encodes the AND relation of the given arguments
	 */
	public static <V extends Variable> AndVariable generateAndVariable(Set<V> arguments, Set<Constraint> constraints) {
		Variable[] inputs = arguments.toArray(new Variable[arguments.size()]);
		AndVariable result = Variables.varAndVariable(inputs);
		constraints.addAll(generateAndConstraints(arguments, result));
		return result;
	}

	/**
	 * Generates a set of {@link Constraint}s that encode an AND relationship
	 * between the {@link Variable}s provided as arguments and the variable provided
	 * as result. Example: arguments = (A, B, C); result = D; The generated
	 * constraints then encode the relation A and B and C = D.
	 * 
	 * @param arguments
	 *            the set of {@link Variable}s that shall affect the activation of
	 *            the result variable to be their AND operation
	 * @param result
	 *            the {@link Variable} that shall be activated or deactivated in
	 *            accordance to the result of an AND operation of the argument
	 *            variables
	 * @return the set of {@link Constraint}s that encode an AND relationship
	 *         between the {@link Variable}s provided as arguments and the variable
	 *         provided as result
	 */
	public static <V extends Variable> Set<Constraint> generateAndConstraints(Set<V> arguments, Variable result) {
		Set<Constraint> cs = new HashSet<Constraint>();
		for (V arg : arguments) {
			cs.add(Constraints.generateNegativeImplication(arg, result));
		}
		cs.add(generateDistributedActivationConstraint(arguments, result));
		return cs;
	}

	/**
	 * Generates the {@link Constraint} stating that the result {@link Variable} has
	 * to be active if all of the condition {@link Variable}s are active. Example:
	 * conditions = (A,B); result = C; A = 1, B = 1 => C = 1
	 * 
	 * @param conditions
	 *            the result {@link Variable} is activated if all of these
	 *            {@link Variable}s are active
	 * @param result
	 *            the {@link Variable} that shall be activated if all condition
	 *            {@link Variable}s are activated
	 * @return the {@link Constraint} stating that the result {@link Variable} has
	 *         to be active if all the condition {@link Variable}s are active
	 */
	public static <V extends Variable> Constraint generateDistributedActivationConstraint(Set<V> conditions,
			Variable result) {
		Constraint c = new Constraint(Operator.GE, 0);
		for (V condition : conditions) {
			c.add(Variables.n(condition));
		}
		c.add(-1, Variables.n(result));
		return c;
	}

	/**
	 * Generates the {@link Constraint} stating that the implicated {@link Variable}
	 * is automatically deactivated if the condition variable is not active.
	 * 
	 * @param condition
	 *            the condition variable
	 * @param implication
	 *            the implication variable
	 * @return the {@link Constraint} stating that the implicated {@link Variable}
	 *         is automatically deactivated if the condition variable is not active
	 */
	public static Constraint generateNegativeImplication(Variable condition, Variable implication) {
		Constraint result = new Constraint(Operator.LE, 0);
		result.add(net.sf.opendse.optimization.encoding.variables.Variables.p(implication));
		result.add(-1, Variables.p(condition));
		return result;
	}

	/**
	 * Generates the {@link Constraint} stating that exactly n of the given argument
	 * {@link Variable}s have to be active, while the rest is deactivated.
	 * 
	 * @param arguments
	 *            the {@link Variable}s that can be active or not
	 * @param n
	 *            the exact number of {@link Variable}s that have to be active
	 * @return the {@link Constraint} stating that exactly n of the given argument
	 *         {@link Variable}s have to be active, while the rest is deactivated
	 */
	public static <V extends Variable> Constraint generatePickExactlyNConstraint(Set<V> arguments, int n) {
		Constraint result = new Constraint(Operator.EQ, n);
		for (V arg : arguments) {
			result.add(net.sf.opendse.optimization.encoding.variables.Variables.p(arg));
		}
		return result;
	}

	/**
	 * Generates a set of {@link Constraint}s that encode an OR relationship between
	 * the {@link Variable}s provided as arguments and the variable provided as
	 * result. Example: arguments = (A, B, C); result = D; The generated constraints
	 * then encode the relation A or B or C = D.
	 * 
	 * @param arguments
	 *            the set of {@link Variable}s that shall affect the activation of
	 *            the result variable to be their OR operation
	 * @param result
	 *            the {@link Variable} that shall be activated or deactivated in
	 *            accordance to the result of an OR operation of the argument
	 *            variables
	 * @return the set of {@link Constraint}s that encode an OR relationship between
	 *         the {@link Variable}s provided as arguments and the variable provided
	 *         as result
	 */
	public static <V extends Variable> Set<Constraint> generateOrConstraints(Set<V> arguments, Variable result) {
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
	public static <V extends Variable> Constraint generateMinimalRequirementConstraint(Set<V> conditions,
			Variable result) {
		Constraint minimalRequirementConstraint = new Constraint(Operator.LE, 0);
		minimalRequirementConstraint.add(Variables.p(result));
		for (Variable condition : conditions) {
			minimalRequirementConstraint.add(-1, Variables.p(condition));
		}
		return minimalRequirementConstraint;
	}

	/**
	 * Generates the {@link Constraint} expressing that the two given
	 * {@link Variable}s have to be equal.
	 * 
	 * @param first
	 *            the first {@link Variable}
	 * @param second
	 *            the second {@link Variable}
	 * @return the {@link Constraint} expressing that the two given
	 *         {@link Variable}s have to be equal
	 */
	public static Constraint generateEqualityConstraint(Variable first, Variable second) {
		Constraint result = new Constraint(Operator.EQ, 0);
		result.add(Variables.p(first));
		result.add(-1, Variables.p(second));
		return result;
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
