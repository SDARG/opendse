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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
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
