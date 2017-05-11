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
}
