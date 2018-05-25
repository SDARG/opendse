package net.sf.opendse.model;

import java.util.HashMap;

/**
 * The {@code ResourceTypes} is a map of different resource types.
 * 
 * @author Valentina Richthammer
 *
 * @param <R>
 *            the type of resource
 */
public class ResourceTypes<R extends Resource> extends HashMap<String, R> {

	private static final long serialVersionUID = 1L;

}
