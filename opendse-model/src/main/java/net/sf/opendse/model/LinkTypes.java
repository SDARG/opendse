package net.sf.opendse.model;

import java.util.HashMap;

/**
 * The {@code LinkTypes} is a map of different link types.
 * 
 * @author Valentina Richthammer
 * @param <R>
 * 
 * @param String
 *            the id of the link type
 * @param <R>
 *            the type of link
 */
public class LinkTypes<L extends Link> extends HashMap<String, L> {

	private static final long serialVersionUID = 1L;

}
