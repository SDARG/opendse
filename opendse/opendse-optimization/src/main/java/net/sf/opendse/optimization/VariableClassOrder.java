package net.sf.opendse.optimization;

import java.util.ArrayList;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The {@link VariableClassOrder} contains variable classes which are explored by the genetic algorithm during the
 * design space exploration. The order of the variable classes defines in which order the corresponding variables are
 * set by the SAT solver during the decoding of the SAT genotype.
 * <p>
 * Only variables assignable from classes found in the {@link VariableClassOrder} are used by the genetic algorithm. The
 * assignment of variables which are not assignable from the classes in the {@link VariableClassOrder} (in this case,
 * the indexOf() method returns {@code -1}) is not explored, but instead chosen solely by the SAT-solver.
 * <p>
 * During the SAT-solving, variables that are assignable from classes with a lower index in the
 * {@link VariableClassOrder} are set before variables with a higher index, while variables which are not assignable at
 * all are set at the very end.
 * 
 * @author Fedor Smirnov
 *
 */
@Singleton
public class VariableClassOrder {

	private final ArrayList<Class<?>> order = new ArrayList<Class<?>>();

	/**
	 * In the default case, the {@link Object} class is the only entry of the {@link VariableClassOrder}.
	 * Consequently,<br>
	 * a) all variables are explored by the genetic algorithm and<br>
	 * b) no fix order is specified for setting the variables by the SAT-solver.
	 */
	@Inject
	public VariableClassOrder() {
		order.add(Object.class);
	}

	/**
	 * Returns the number of the list entries in the order list.
	 * 
	 * @return Number of classes in the order list.
	 */
	public int getOrderSize() {
		return order.size();
	}

	/**
	 * Returns the lowest index of a class from which the given object is assignable. Returns a -1 if the given object
	 * is not assignable from any of the classes that can be found in the order list.
	 * 
	 * @param object
	 * @return
	 */
	public int indexOf(Object object) {
		for (int i = 0; i < this.order.size(); i++) {
			if (this.order.get(i).isAssignableFrom(object.getClass())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Add the given variable class to the variable order. (Optional:) Provide this variable with a lower index than the
	 * provided variables classes. All variable classes that are not assignable from the {@link VariableClassOrder}
	 * after this operation are ignored, i.e., they are not part of the SAT-genotype and are set after all non-ignored
	 * variables by the SAT-solver.
	 * 
	 * @param variableClass
	 *            The variable class that is added to the variable order
	 * @param beforeVariableClasses
	 *            (optional) The variable classes that should have a higher list index than the variableClass and should
	 *            be set after variableClass
	 */
	public void add(Class<?> variableClass, Class<?>... beforeVariableClasses) {
		// remove the Object.class if it is in the list
		if (order.contains(Object.class)) {
			order.remove(Object.class);
		}
		if (beforeVariableClasses.length == 0) {
			// no constraints about the order => the new variable class is added
			// at the end
			this.order.add(variableClass);
		} else {
			// find the minimal index of the variable classes that are to be
			// decided after the given variable
			int minimalIndex = order.size();
			for (Class<?> beforeVariableClass : beforeVariableClasses) {
				int index = order.indexOf(beforeVariableClass);
				if (index != -1) {
					minimalIndex = Math.min(minimalIndex, index);
				}
			}
			order.add(minimalIndex, variableClass);
		}
	}
}
