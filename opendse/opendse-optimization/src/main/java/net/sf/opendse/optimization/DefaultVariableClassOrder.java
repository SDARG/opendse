package net.sf.opendse.optimization;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
/**
 * Default variable order, where there is no order distinction between the
 * different variable types.
 * 
 * @author Fedor Smirnov
 */
public class DefaultVariableClassOrder extends VariableClassOrder {

	private static final long serialVersionUID = 1L;

	@Inject
	public DefaultVariableClassOrder() {
		super();
		this.addVariableClass(Object.class);
	}

//	/**
//	 * Add the given Variable class to the variable order used by the SAT solver
//	 * without specifying any variables that have to be decided later. The
//	 * class2add may become the variable that is decided last.
//	 * 
//	 * @param class2add
//	 */
//	public void addVariableClass(Class<?> class2add) {
//		addVariableClass(class2add, new HashSet<Class<?>>());
//	}
//
//	/**
//	 * Add the given Variable class to the variable order used by the SAT
//	 * solver.
//	 * 
//	 * @param class2add
//	 *            : the variable class to add
//	 * @param classesDecidedLater
//	 *            : a set of variable classes that have to be decided later
//	 */
//	public void addVariableClass(Class<?> class2add, Set<Class<?>> classesDecidedLater) {
//		if (!useVariableOrder) {
//			throw new IllegalArgumentException("Variable order currently disabled.");
//		}
//		int index = Integer.MAX_VALUE;
//		// iterate the successors
//		for (Class<?> classDecidedLater : classesDecidedLater) {
//			// check whether there is an order contradiction
//			if (precedenceMap.keySet().contains(classDecidedLater)) {
//				if (precedenceMap.get(classDecidedLater).contains(class2add)) {
//					throw new IllegalArgumentException("Contradiction in the order relation of class "
//							+ class2add.toString() + " and class " + classDecidedLater.toString());
//				}
//			}
//			// note the order relation into the map
//			if (!precedenceMap.containsKey(class2add)) {
//				precedenceMap.put(class2add, new HashSet<Class<?>>());
//			}
//			precedenceMap.get(class2add).add(classDecidedLater);
//			// update the index
//			int otherIndex = indexOf(classDecidedLater);
//			if (otherIndex != -1) {
//				index = Math.min(index, otherIndex);
//			}
//		}
//		// make a set containing all classes with order
//		List<Class<?>> knownOrder = new ArrayList<Class<?>>(this);
//		knownOrder.add(class2add);
//		// make a new class list
//		this.clear();
//		// go through the contained classes
//		for (Class<?> cl : knownOrder) {
//			if (this.isEmpty()) {
//				this.add(cl);
//			} else {
//				// iterate the list from the start
//				boolean entered = false;
//				for (int ind = 0; ind < this.size(); ind++) {
//					Class<?> classAtPos = this.get(ind);
//					if (precedenceMap.containsKey(cl) && precedenceMap.get(cl).contains(classAtPos)) {
//						// the current class has to be in front of the one at
//						// the current position => add it at the current pos
//						this.add(ind, cl);
//						entered = true;
//						break;
//					}
//				}
//				if (!entered) {
//					// not yet entered => the current class has no successors in
//					// the current list and can be entered at the very end
//					this.add(cl);
//				}
//			}
//		}
//		assert this.size() == knownOrder.size();
//	}

	
}
