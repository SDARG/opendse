package net.sf.opendse.visualization;

import java.awt.Color;

import net.sf.opendse.model.Node;

/**
 * Defines a color scheme for an {@link AbstractGraphPanelFormat}.
 * 
 * @author Felix Reimann
 *
 */
public interface ColorModel {

	/**
	 * Returns the {@link Color} for the given {@link Node}.
	 * 
	 * @param node
	 *            the node
	 * @return the color of the node
	 */
	public Color get(Node node);

}
