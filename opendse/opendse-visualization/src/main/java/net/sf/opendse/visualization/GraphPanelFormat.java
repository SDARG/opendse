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
package net.sf.opendse.visualization;

import java.awt.Color;
import java.awt.Shape;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Graph;
import net.sf.opendse.model.Node;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public interface GraphPanelFormat {
	
	public Graph<Node,Edge> getGraph();

	public Layout<Node, LocalEdge> getLayout(DirectedGraph<Node, LocalEdge> graph);

	public Color getColor(Node node);

	public Color getColor(Edge edge);
	
	public Shape getShape(Node node);
	
	public int getSize(Node node);

	public boolean isActive(Node node);

	public boolean isActive(Edge edge, Node n0, Node n1);

	public String getTooltip(Node node);

	public String getTooltip(Edge edge);
	
	public Shape getSymbol(Node node);
	
	public Position getLabelPosition(Node node);
	
	public boolean drawEdge(Edge edge);

}
