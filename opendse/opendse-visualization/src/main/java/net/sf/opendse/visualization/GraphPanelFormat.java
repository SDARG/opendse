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
