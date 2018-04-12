package net.sf.opendse.encoding.variables;

/**
 * The {@link ApplicationAlternative} variable encodes whether a certain
 * alternative implementation of an application function is activated (1) or
 * not.
 * 
 * @author Fedor Smirnov
 *
 */
public class ApplicationAlternative extends Variable {

	protected ApplicationAlternative(String functionName, String functionId) {
		super(functionName, functionId);
	}

	public String getFunctionName() {
		return get(0);
	}

	public String getFunctionId() {
		return get(1);
	}
}
