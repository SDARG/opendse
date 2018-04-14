package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link ColoredCommNode} {@link Variable} encodes whether a
 * {@link Resource} node in a routing {@link Architecture} graph of a
 * communication {@link Task} is colored in a certain color (1) or not (0).
 * 
 * @author Fedor Smirnov
 *
 */
public class ColoredCommNode extends Variable {

	protected ColoredCommNode(Task communication, Resource resource, String color) {
		super(communication, resource, color);
	}

	public Task getCommunication() {
		return get(0);
	}

	public Resource getResource() {
		return get(1);
	}

	public String getColor() {
		return get(2);
	}

}
