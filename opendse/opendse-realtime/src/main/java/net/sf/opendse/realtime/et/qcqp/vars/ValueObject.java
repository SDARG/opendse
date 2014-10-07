package net.sf.opendse.realtime.et.qcqp.vars;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code ValueObject} is a superclass for all comparable objects based on
 * their fields. Extend from this class to create unique variable identifiers.
 * 
 * @author lukasiewycz
 * 
 */
public abstract class ValueObject {

	protected static Map<Class<?>, Field[]> fields = Collections
			.synchronizedMap(new HashMap<Class<?>, Field[]>());

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getClass().hashCode();
		for (Field field : getFields()) {
			try {
				Object obj = field.get(this);
				result = prime * result + obj.hashCode();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ValueObject other = (ValueObject) obj;
		for (Field field : getFields()) {
			try {
				Object o1 = field.get(this);
				Object o2 = field.get(other);
				if (!o1.equals(o2)) {
					return false;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		s += this.getClass().getSimpleName();
		s += "[";
		for (Field field : getFields()) {
			try {
				Object obj = field.get(this);
				s += obj + ",";
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		s = s.substring(0, s.length() - 1);
		s += "]";
		return s;
	}

	/**
	 * Returns all fields of this class.
	 * 
	 * @return the fields of this class
	 */
	protected Field[] getFields() {
		Class<?> clazz = this.getClass();
		Field[] f = fields.get(clazz);
		if (f == null) {
			return createFields(clazz);
		} else {
			return f;
		}
	}

	/**
	 * Returns all fields of the given class.
	 * 
	 * @param clazz
	 *            the given class
	 * @return the fields
	 */
	protected synchronized Field[] createFields(Class<?> clazz) {
		List<Field> f = new ArrayList<Field>();
		Class<?> c = clazz;
		while (c != ValueObject.class) {
			Field[] df = c.getDeclaredFields();
			f.addAll(Arrays.asList(df));
			c = c.getSuperclass();
		}
		Field[] fa = new Field[f.size()];
		f.toArray(fa);
		for (Field field : fa) {
			field.setAccessible(true);
		}
		fields.put(clazz, fa);
		return fa;
	}

}
