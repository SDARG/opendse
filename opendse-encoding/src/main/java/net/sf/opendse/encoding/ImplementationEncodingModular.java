package net.sf.opendse.encoding;

import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.optimization.encoding.ImplementationEncoding;
import net.sf.opendse.encoding.variables.InterfaceVariable;

/**
 * The {@link ImplementationEncodingModular} generates the {@link Constraint}s
 * describing a valid implementation. Hereby, the encoding is done by separated
 * encoding modules that exchange information solely by accessing the
 * {@link InterfaceVariable}s.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ImplementationEncodingModular extends ImplementationEncoding {

	/**
	 * return the {@link InterfaceVariable}s encoding the implementation information
	 * 
	 * @return the set of {@link InterfaceVariable}s describing the encoded
	 *         implementation
	 */
	public Set<InterfaceVariable> getInterfaceVariables();
}
