package net.sf.opendse.optimization.encoding;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.sf.opendse.encoding.firm.variables.CLRR;
import net.sf.opendse.encoding.firm.variables.CR;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;

public class VariableComparator implements Comparator<Object> {

	protected static final long serialVersionUID = 1L;

	public static List<Class<?>> order = Arrays.<Class<?>>asList(Resource.class, Link.class, Mapping.class, CR.class,
			CLRR.class);

	protected Integer order(Object obj) {
		int i = 0;
		for (Class<?> clazz : order) {
			if (clazz.isAssignableFrom(obj.getClass())) {
				return i;
			}
			i++;
		}
		return 100;
	}

	@Override
	public int compare(Object o0, Object o1) {
		return order(o0).compareTo(order(o1));
	}
}
