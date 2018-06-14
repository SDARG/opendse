package net.sf.opendse.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link ResourceTypes} is a {@link Map} of different types of
 * {@link Resource}s.
 * 
 * @author Valentina Richthammer
 *
 * @param <R>
 *            the type of resource
 */
public class ResourceTypes<R extends Resource> extends HashMap<String, R> {

	private static final long serialVersionUID = 1L;

}
