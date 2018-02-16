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
package net.sf.opendse.realtime.et.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class TimingGraph extends DirectedSparseMultigraph<TimingElement, TimingDependency> implements Iterable<TimingElement> {

	private static final long serialVersionUID = 1L;

	public Set<TimingDependencyPriority> getPriorityEdges() {
		Set<TimingDependencyPriority> edges = new HashSet<TimingDependencyPriority>();
		for (TimingDependency td : this.getEdges()) {
			if (td instanceof TimingDependencyPriority) {
				edges.add((TimingDependencyPriority) td);
			}
		}
		return edges;
	}

	public TimingElement findNode(TimingElement timingElement) {
		for (TimingElement te : this.getVertices()) {
			if (te.equals(timingElement)) {
				return te;
			}
		}
		return null;
	}

	@Override
	public Iterator<TimingElement> iterator() {
		return this.getVertices().iterator();
	}

	public int getPriorityEdgeCount() {
		return getPriorityEdges().size();
	}

}
