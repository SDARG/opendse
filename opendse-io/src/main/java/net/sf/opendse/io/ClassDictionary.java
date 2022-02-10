package net.sf.opendse.io;

/**
 * The {@link ClassDictionary} is used to look up class-related information
 * about the classes modeling elements of the specification, such as their
 * string names.
 * 
 * @author Fedor Smirnov
 *
 */
public interface ClassDictionary {

	/**
	 * Returns the (canonical) name of a {@code Class}.
	 * 
	 * @param clazz the Class object
	 * @return the canonical name of the clazz
	 */
	String getType(Class<?> clazz);

	/**
	 * Returns true iff the given name is in the class map.
	 * 
	 * @param name the given name
	 * @return true iff the given name is in the class map
	 */
	boolean hasClassName(String name);

	/**
	 * Returns the class associated with the given name
	 * 
	 * @param name the given name
	 * @return the class associated with the given name
	 */
	Class<?> getClass(String name);

	/**
	 * Returns true iff {@code Class} is part of the primitives.
	 * 
	 * @param clazz the Class object
	 * @return true iff clazz is a primitive
	 */
	boolean isPrimitive(Class<?> clazz);
}
