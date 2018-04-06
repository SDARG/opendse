package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.optimization.encoding.variables.Variable;

/**
 * Variable representing the activation of one of several mutually exclusive
 * application options.
 * 
 * AAA = 1 => the alternative with the specified name and id is active in the
 * implementation.
 * 
 * @author Fedor Smirnov
 *
 */
public class ApplicationAlternativeActivation extends Variable {

	protected ApplicationAlternativeActivation(String alternativeName, String alternativeId) {
		super(alternativeName, alternativeId);
	}

	public String getAlternativeName() {
		return get(0);
	}

	public String getAlternativeId() {
		return get(1);
	}
}
