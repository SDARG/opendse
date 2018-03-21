package net.sf.opendse.encoding.variables;

import java.util.HashMap;
import java.util.Map;

import org.opt4j.satdecoding.Literal;

import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * contains static methods for the creation and maintenance of encoding varaibles
 * 
 * @author Fedor Smirnov
 *
 */
public class Variables {

	protected static final Map<Variable, Literal> pCache = new HashMap<Variable, Literal>();
	protected static final Map<Variable, Literal> nCache = new HashMap<Variable, Literal>();
	
	private Variables() {
	}
	
	public static M var(Mapping<Task, Resource> mapping) {
		return new M(mapping);
	}
	
	public static DTT var(Dependency dependency, Task sourceTask, Task destinationTask) {
		return new DTT(dependency, sourceTask, destinationTask);
	}
	
	public static T var(Task task) {
		return new T(task);
	}
	
	/**
	 * returns the positive literal for the given variable
	 * 
	 * @param variable
	 * @return the positive literal
	 */
	public static Literal p(Variable variable) {
		if (!pCache.containsKey(variable)) {
			pCache.put(variable, new Literal(variable, true));
		}
		return pCache.get(variable);
	}
	
	/**
	 * returns the negative literal for the given variable
	 * 
	 * @param variable
	 * @return the negative literal for the given variable
	 */
	public static Literal n(Variable variable) {
		if (!nCache.containsKey(variable)) {
			nCache.put(variable, new Literal(variable, false));
		}
		return nCache.get(variable);
	}
}
