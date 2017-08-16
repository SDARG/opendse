package net.sf.opendse.optimization;

import com.google.inject.Inject;

import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.optimization.encoding.Encoding;
import net.sf.opendse.optimization.encoding.variables.CR;
import net.sf.opendse.optimization.encoding.variables.EAVI;

/**
 * When bound during an exploration, the {@link RoutingVariableClassOrder}
 * adjusts the {@link VariableClassOrder} to an order beneficial for the
 * constraints defined in {@link Encoding}.
 * 
 * @author Fedor Smirnov
 *
 */
public class RoutingVariableClassOrder {
	@Inject
	public RoutingVariableClassOrder(VariableClassOrder order) {
		order.add(Resource.class);
		order.add(Link.class);
		order.add(EAVI.class);
		order.add(Mapping.class);
		order.add(CR.class);
	}
}
