package net.sf.opendse.visualization;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import net.sf.opendse.model.Node;

import com.google.inject.Singleton;

@Singleton
public class ColorModelArchitecture implements ColorModel {

	Map<String, Color> colors = new HashMap<String, Color>();

	{
		colors.put("ECU", Graphics.STEELBLUE);
		colors.put("CAN", Graphics.LIGHTSALMON);
		colors.put("FlexRay", Graphics.ROSYBROWN);
		colors.put("Sensor", Graphics.DODGERBLUE);
		colors.put("Actuator", Graphics.DODGERBLUE);
		colors.put("Gateway", Graphics.SADDLEBROWN);
	}

	@Override
	public Color get(Node node) {
		if (node.getType() != null) {
			return colors.get(node.getType());
		}
		return Graphics.STEELBLUE;
	}

	/**
	 * Register a {@link Color} for a {@link Node} type.
	 * 
	 * @param type
	 *            the type of the node
	 * @param color
	 *            the color to paint the node
	 * 
	 * @see Node#getType()
	 */
	public void registerColor(String type, Color color) {
		colors.put(type, color);
	}
}
