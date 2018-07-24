package net.sf.opendse.encoding.module;

import com.google.inject.Inject;

import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.L;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.R;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.optimization.VariableClassOrder;
import net.sf.opendse.optimization.encoding.Encoding;
import net.sf.opendse.optimization.encoding.variables.EAVI;

/**
 * When bound during an exploration, the {@link RoutingVariableClassOrder}
 * adjusts the {@link VariableClassOrder} to an order beneficial for the
 * constraints defined in {@link Encoding}.
 * 
 * @author Fedor Smirnov
 *
 */
public class VariableClassOrderModular {
	@Inject
	public VariableClassOrderModular(VariableClassOrder order) {
		order.add(T.class);
		order.add(DTT.class);
		order.add(R.class);
		order.add(L.class);
		order.add(M.class);
		order.add(CR.class);
		order.add(CLRR.class);
		order.add(EAVI.class);
	}
}
