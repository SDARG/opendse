package net.sf.opendse.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link LinkTypes} is a {@link Map} of different types of {@link Link}s.
 * 
 * @author Valentina Richthammer
 * 
 * @param <L>
 *            the type of link
 */
public class LinkTypes<L extends Link> extends HashMap<String, L> {

	private static final long serialVersionUID = 1L;

}
