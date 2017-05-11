package net.sf.opendse.optimization;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.encoding.Encoding;
import net.sf.opendse.optimization.encoding.variables.CR;
import net.sf.opendse.optimization.encoding.variables.EAVI;

/**
 * The variable order used for a design space exploration following the
 * constraints defined by {@link Encoding}.
 * 
 * @author Fedor Smirnov
 *
 */
public class RoutingVariableClassOrder extends VariableClassOrder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoutingVariableClassOrder() {
		super();
		addVariableClass(Resource.class);
		addVariableClass(Link.class);
		addVariableClass(EAVI.class);
		addVariableClass(Mapping.class);
		addVariableClass(CR.class);
	}
}
