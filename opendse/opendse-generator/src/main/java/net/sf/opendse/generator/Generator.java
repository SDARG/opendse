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
package net.sf.opendse.generator;

import java.util.List;
import java.util.Random;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Graph;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Task;

public abstract class Generator {

	public enum Type {
		NODE, PROCESS, COMMUNICATION, EDGE, ELEMENT;
	}

	protected final Random random;

	public Generator(Random random) {
		this.random = random;
	}

	public int rand(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	public <V> V rand(List<V> list) {
		return list.get(random.nextInt(list.size()));
	}

	public void annotateAttribute(Graph<?, ?> graph, String attribute, int min, int max, Type type) {
		if (type == Type.NODE || type == Type.PROCESS || type == Type.COMMUNICATION || type == Type.ELEMENT) {
			for (Node n : graph.getVertices()) {
				if(type == Type.PROCESS && n instanceof Task && Models.isProcess((Task)n)){
					n.setAttribute(attribute, rand(min, max));
				} else if (type == Type.COMMUNICATION && n instanceof Task && Models.isCommunication((Task)n)){
					n.setAttribute(attribute, rand(min,max));
				} else {
					n.setAttribute(attribute, rand(min,max));
				}
			}
		}
		if (type == Type.EDGE || type == Type.ELEMENT) {
			for (Edge e : graph.getEdges()) {
				e.setAttribute(attribute, rand(min, max));
			}
		}
	}

}
