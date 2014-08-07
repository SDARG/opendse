package net.sf.opendse.visualization;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Node;
import edu.uci.ics.jung.graph.util.EdgeType;

public class LocalEdge {
	final Node source;
	final Node dest;
	final Edge edge;
	final EdgeType type;

	public LocalEdge(Edge edge, Node source, Node dest, EdgeType type) {
		super();
		this.source = source;
		this.dest = dest;
		this.edge = edge;
		this.type = type;
	}

	public Node getSource() {
		return source;
	}

	public Node getDest() {
		return dest;
	}

	public Edge getEdge() {
		return edge;
	}

	public EdgeType getType() {
		return type;
	}

}