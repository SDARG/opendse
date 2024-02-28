/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.IAttributes;

/**
 * The {@code Common} class contains common methods for reading and writing a
 * {@code Specification}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Common {

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

	/**
	 * Returns an instance of a given {@code Class}.
	 * 
	 * @param value
	 * 
	 * @param clazz
	 *            the Class object
	 * @return the instance of the given class
	 */
	@SuppressWarnings("rawtypes")
	public static Object toInstance(String value, Class<?> clazz) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		if (!clazz.isEnum()) {
			Constructor constructor = clazz.getConstructor(String.class);
			if (constructor != null) {
				return constructor.newInstance(value.trim());
			}
		} else {
			Class<? extends Enum> eclazz = clazz.asSubclass(Enum.class);
			for (Enum e : eclazz.getEnumConstants()) {
				if (e.name().equalsIgnoreCase(value.trim())) {
					return e;
				}
			}
		}
		return null;
	}

	/**
	 * Sets the {@code Attributes}.
	 * 
	 * @param e
	 * 			the IAttributes
	 * @param attributes
	 *            the attributes to set
	 */
	public static void setAttributes(IAttributes e, Attributes attributes) {
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
	 *             thrown in case of an IO error
	 * @throws ClassNotFoundException
	 *             thrown in case the class does not exist
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
	 *             thrown in case of an IO error
	 */
	public static String toString(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();

		return new String(Base64Coder.encode(baos.toByteArray()));

	}
}
