package net.sf.opendse.optimization;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.inject.ImplementedBy;

/**
 * The {@link VariableClassOrder} specifies the order in which the different
 * variable types are set by the SAT-solver during the constraint solving.
 * 
 * @author Fedor Smirnov, Felix Reimann
 *
 */
@ImplementedBy(DefaultVariableClassOrder.class)
public abstract class VariableClassOrder extends ArrayList<Class<?>> implements Comparator<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Returns the index of the class of the given object in the order list.
	 * Returns -1 if the class is not in the order list.
	 * 
	 */
	public int indexOf(Object object) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).isAssignableFrom(object.getClass())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Add the given variable class to the variable order.
	 * 
	 * @param variableClass
	 *            The variable class that is added to the variable order
	 * @param beforeVariableClasses
	 *            (optional) The variable classes that should be set after the
	 *            added variableClass
	 */
	protected void addVariableClass(Class<?> variableClass, Class<?>... beforeVariableClasses) {
		if (beforeVariableClasses.length == 0) {
			// no constraints about the order => the new variable class is added
			// at the end
			this.add(variableClass);
		} else {
			// find the minimal index of the variable classes that are to be
			// decided after the given variable
			int minimalIndex = this.size();
			for (Class<?> beforeVariableClass : beforeVariableClasses) {
				int index = indexOf(beforeVariableClass);
				if (index != -1) {
					minimalIndex = Math.min(minimalIndex, index);
				}
			}
			this.add(minimalIndex, variableClass);
		}
	}

	@Override
	public int compare(Object first, Object second) {
		int firstInd = (indexOf(first) == -1) ? Integer.MAX_VALUE : indexOf(first);
		int secondInd = (indexOf(second) == -1) ? Integer.MAX_VALUE : indexOf(second);
		int result = (firstInd < secondInd) ? -1 : ((firstInd == secondInd) ? 0 : 1);
		return result;
	}
}
