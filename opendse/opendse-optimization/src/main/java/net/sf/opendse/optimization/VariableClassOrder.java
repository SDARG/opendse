package net.sf.opendse.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.encoding.variables.CR;
import net.sf.opendse.optimization.encoding.variables.EAVI;

@Singleton
/**
 * 
 * @author Fedor Smirnov Class containing the information about the order in
 *         which the SAT solver sets the different variables during the
 *         constraint solving.
 */
public class VariableClassOrder extends ArrayList<Class<?>> {

	private static final long serialVersionUID = 1L;
	private final boolean useVariableOrder;
	// map containing precedence relations that have to be respected. Mapping is
	// performed from the predecessor (the variable which is decided first) to
	// the set of the successors (the variables that are decided later)
	private Map<Class<?>, Set<Class<?>>> precedenceMap = new HashMap<Class<?>, Set<Class<?>>>();

	@Inject
	public VariableClassOrder(
			@Constant(value = "variableorder", namespace = SATCreatorDecoder.class) boolean useVariableOrder) {
		super();
		this.useVariableOrder = useVariableOrder;
		if (useVariableOrder) {
			Set<Class<?>> successors = new HashSet<Class<?>>();
			this.add(CR.class);
			successors.add(CR.class);
			this.addVariableClass(Mapping.class, successors);
			successors.add(Mapping.class);
			this.addVariableClass(EAVI.class, successors);
			successors.add(EAVI.class);
			this.addVariableClass(Link.class, successors);
			successors.add(Link.class);
			this.addVariableClass(Resource.class, successors);
		} else {
			this.add(Object.class);
		}
	}

	public void addVariableClass(Class<?> class2add) {
		addVariableClass(class2add, new HashSet<Class<?>>());
	}

	/**
	 * Add the given Variable class to the variable order used by the SAT
	 * solver.
	 * 
	 * @param class2add
	 *            : the variable class to add
	 * @param classesDecidedLater
	 *            : a set of variable classes that have to be decided later
	 */
	public void addVariableClass(Class<?> class2add, Set<Class<?>> classesDecidedLater) {
		if (!useVariableOrder) {
			throw new IllegalArgumentException("Variable order currently disabled.");
		}
		int index = Integer.MAX_VALUE;
		// iterate the successors
		for (Class<?> classDecidedLater : classesDecidedLater) {
			// check whether there is an order contradiction
			if (precedenceMap.keySet().contains(classDecidedLater)) {
				if (precedenceMap.get(classDecidedLater).contains(class2add)) {
					throw new IllegalArgumentException("Contradiction in the order relation of class "
							+ class2add.toString() + " and class " + classDecidedLater.toString());
				}
			}
			// note the order relation into the map
			if (!precedenceMap.containsKey(class2add)) {
				precedenceMap.put(class2add, new HashSet<Class<?>>());
			}
			precedenceMap.get(class2add).add(classDecidedLater);
			// update the index
			int otherIndex = indexOf(classDecidedLater);
			if (otherIndex != -1) {
				index = Math.min(index, otherIndex);
			}
		}
		// make a set containing all classes with order
		List<Class<?>> knownOrder = new ArrayList<Class<?>>(this);
		knownOrder.add(class2add);
		// make a new class list
		this.clear();
		// go through the contained classes
		for (Class<?> cl : knownOrder) {
			if (this.isEmpty()) {
				this.add(cl);
			} else {
				// iterate the list from the start
				boolean entered = false;
				for (int ind = 0; ind < this.size(); ind++) {
					Class<?> classAtPos = this.get(ind);
					if (precedenceMap.containsKey(cl) && precedenceMap.get(cl).contains(classAtPos)) {
						// the current class has to be in front of the one at
						// the current position => add it at the current pos
						this.add(ind, cl);
						entered = true;
						break;
					}
				}
				if (!entered) {
					// not yet entered => the current class has no successors in
					// the current list and can be entered at the very end
					this.add(cl);
				}
			}
		}
		assert this.size() == knownOrder.size();
	}

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
}
