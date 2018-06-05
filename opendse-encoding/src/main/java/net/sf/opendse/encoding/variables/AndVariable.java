package net.sf.opendse.encoding.variables;

/**
 * The {@link AndVariable} encodes the logical AND-relation of two other variables.
 * 
 * @author Fedor Smirnov
 *
 */
public class AndVariable extends Variable {

	protected AndVariable(Variable... variables) {
		super((Object[]) variables);
	}
}
