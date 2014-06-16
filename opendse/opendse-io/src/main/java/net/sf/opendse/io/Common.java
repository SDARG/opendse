/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.IAttributes;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.ParameterRange;
import net.sf.opendse.model.parameter.ParameterSelect;
import net.sf.opendse.model.parameter.ParameterUniqueID;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

/**
 * The {@code Common} class contains common methods for reading and writing a
 * {@code Specification}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Common {

	public static BidiMap<String, Class<?>> classMap = new DualHashBidiMap<String, Class<?>>();

	static {
		classMap.put("INT", Integer.class);
		classMap.put("DOUBLE", Double.class);
		classMap.put("STRING", String.class);
		classMap.put("BOOL", Boolean.class);
		classMap.put("RANGE", ParameterRange.class);
		classMap.put("SELECT", ParameterSelect.class);
		classMap.put("UID", ParameterUniqueID.class);
		classMap.put("resource", Resource.class);
		classMap.put("link", Link.class);
		classMap.put("task", Task.class);
		classMap.put("communication", Communication.class);
		classMap.put("dependency", Dependency.class);
		classMap.put("mapping", Mapping.class);
	}

	protected static Set<Class<?>> primitives = new HashSet<Class<?>>();
	static {
		primitives.add(Boolean.class);
		primitives.add(Byte.class);
		primitives.add(Integer.class);
		primitives.add(Character.class);
		primitives.add(Short.class);
		primitives.add(Float.class);
		primitives.add(Long.class);
		primitives.add(Double.class);
	}

	protected static boolean isPrimitive(Class<?> cls) {
		return cls.isPrimitive() || primitives.contains(cls);
	}

	/**
	 * Transforms an {@code Elements} object into a set of iterable
	 * {@code Element} objects.
	 * 
	 * @param elements
	 *            the elements object
	 * @return the iterable element objects
	 */
	public static Iterable<nu.xom.Element> iterable(final nu.xom.Elements elements) {
		return new Iterable<nu.xom.Element>() {
			@Override
			public Iterator<nu.xom.Element> iterator() {
				return new Iterator<nu.xom.Element>() {
					int c = 0;

					@Override
					public boolean hasNext() {
						return elements.size() > c;
					}

					@Override
					public nu.xom.Element next() {
						return elements.get(c++);
					}

					@Override
					public void remove() {
						throw new RuntimeException("invalid operation: remove");
					}
				};
			}
		};
	}

	protected static String getType(Class<?> clazz) {
		if (classMap.containsValue(clazz)) {
			return classMap.getKey(clazz);
		} else {
			return clazz.getCanonicalName().toString();
		}
	}

	@SuppressWarnings("rawtypes")
	protected static Object toInstance(String value, Class<?> clazz) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (!clazz.isEnum()) {
			return clazz.getConstructor(String.class).newInstance(value.trim());
		} else {
			Class<? extends Enum> eclazz = clazz.asSubclass(Enum.class);
			for (Enum e : eclazz.getEnumConstants()) {
				if (e.name().equalsIgnoreCase(value.trim())) {
					return e;
				}
			}
			return null;
		}
	}

	protected static void setAttributes(IAttributes e, Attributes attributes) {
		for (String name : attributes.keySet()) {
			e.setAttribute(name, attributes.get(name));
		}
	}

	/**
	 * Transforms a Base64 string into an object.
	 * 
	 * @param s
	 *            the string
	 * @return the object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object fromString(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64Coder.decode(s);
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		return o;
	}

	/**
	 * Transforms an object into a Base64 string.
	 * 
	 * @param o
	 *            the object
	 * @return the string
	 * @throws IOException
	 */
	public static String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();

		return new String(Base64Coder.encode(baos.toByteArray()));

	}

}
