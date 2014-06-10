package net.sf.opendse.visualization;

import static net.sf.opendse.visualization.Graphics.mix;
import static net.sf.opendse.visualization.Graphics.tone;

import java.awt.Color;
import java.awt.Shape;
import java.util.Set;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Function;
import net.sf.opendse.model.Graph;
import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.Parameter;
import net.sf.opendse.visualization.GraphPanel.LocalEdge;
import net.sf.opendse.visualization.algorithm.DistanceFlowLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphPanelFormatApplication extends AbstractGraphPanelFormat {

	protected final Application<Task, Dependency> application;
	protected final Mappings<Task, Resource> mappings;
	protected final Routings<Task, Resource, Link> routings;
	protected final Specification specification;
	protected final ElementSelection selection;

	public GraphPanelFormatApplication(Specification specification, ElementSelection selection) {
		super();
		this.specification = specification;
		this.application = convert(specification.getApplication());
		this.mappings = specification.getMappings();
		this.routings = specification.getRoutings();
		this.selection = selection;
	}

	class FunctionTask extends Task {
		protected final Attributes attributes;
		protected final Function<Task, Dependency> function;

		public FunctionTask(Function<Task, Dependency> function) {
			super(function.<String>getAttribute("ID"));
			this.attributes = function.getAttributes();
			this.function = function;
		}

		public Function<Task, Dependency> getFunction() {
			return function;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <O> O getAttribute(String identifier) {
			return (O)attributes.getAttribute(identifier);
		}

		@Override
		public Attributes getAttributes() {
			return attributes;
		}

		@Override
		public Attributes getLocalAttributes() {
			return attributes;
		}

		@Override
		public Set<String> getLocalAttributeNames() {
			return attributes.getAttributeNames();
		}

		@Override
		public void setAttribute(String identifier, Object object) {
			attributes.setAttribute(identifier, object);
		}

		@Override
		public Parameter getAttributeParameter(String identifier) {
			return attributes.getAttributeParameter(identifier);
		}

		@Override
		public Set<String> getAttributeNames() {
			return attributes.getAttributeNames();
		}

	}

	class FunctionDependency extends Dependency {
		public FunctionDependency(String string) {
			super(string);
		}
	}

	protected Application<Task, Dependency> convert(Application<Task, Dependency> original) {
		Application<Task, Dependency> copy = new Application<Task, Dependency>();
		for (Task t : original) {
			copy.addVertex(t);
		}
		for (Dependency d : original.getEdges()) {
			copy.addEdge(d, original.getEndpoints(d), original.getEdgeType(d));
		}

		int d = 0;

		for (Function<Task, Dependency> function : original.getFunctions()) {
			FunctionTask fTask = new FunctionTask(function);
			copy.addVertex(fTask);

			for (Task t : function.getVertices()) {
				copy.addEdge(new FunctionDependency("fd" + (d++)), fTask, t);
			}

		}

		return copy;
	}

	@SuppressWarnings("unchecked")
	public Graph<Node, Edge> getGraph() {
		Graph<?, ?> g = application;
		return (Graph<Node, Edge>) g;
	}

	public Layout<Node, LocalEdge> getLayout(DirectedGraph<Node, LocalEdge> graph) {
		return new DistanceFlowLayout<Node, LocalEdge>(graph);
	}

	@Override
	public Color getColor(Node node) {
		if (node instanceof FunctionTask) {
			return Graphics.GRAY;
		} else if (node instanceof ICommunication) {
			return Graphics.KHAKI;
		} else {
			return Graphics.TOMATO;
		}
	}

	@Override
	public boolean isActive(Node node) {
		if (selection.isNull() || selection.isSelected(node)) {
			return true;
		} else if (selection.get() instanceof Resource) {
			Resource resource = selection.get();
			if (node instanceof Task) {
				Task task = (Task) node;

				if (task instanceof ICommunication) {
					return routings.get(task).containsVertex(resource);
				} else {
					return mappings.getSources(resource).contains(task);
				}
			}
		} else if (selection.get() instanceof Mapping<?, ?>) {
			Mapping<Task, Resource> m = selection.get();
			return node.equals(m.getSource());
		} else if (selection.get() instanceof FunctionTask) {
			Function<Task, Dependency> function = ((FunctionTask) selection.get()).getFunction();
			return function.containsVertex((Task)node);
		}
		return false;
	}

	@Override
	public boolean isActive(Edge edge, Node n0, Node n1) {
		Pair<Task> endpoints = application.getEndpoints((Dependency) edge);
		Task t0 = endpoints.getFirst();
		Task t1 = endpoints.getSecond();
		return isActive(t0) && isActive(t1);
	}

	@Override
	public Color getColor(Edge edge) {
		Pair<Task> endpoints = application.getEndpoints((Dependency) edge);
		Task t0 = endpoints.getFirst();
		Task t1 = endpoints.getSecond();
		return tone(mix(getColor(t0), getColor(t1)), -0.5);
	}

	@Override
	public Shape getShape(Node node) {
		Task task = (Task) node;
		if (node instanceof FunctionTask) {
			return shapes.getRegularStar(node, 5);
		} else if (Models.isCommunication(task)) {
			return shapes.getRoundRectangle(node);
		} else {
			return shapes.getEllipse(node);
		}
	}

	@Override
	public int getSize(Node node) {
		Task task = (Task) node;
		if (Models.isCommunication(task)) {
			return 10;
		} else {
			return 20;
		}
	}

	public Shape getSymbol(Node node) {
		if ("Sensor".equals(node.getType())) {
			return shapes.getInnerOut(node);
		} else if ("Actuator".equals(node.getType())) {
			return shapes.getInnerIn(node);
		} else {
			return null;
		}
	}

	@Override
	public Position getLabelPosition(Node node) {
		if (node instanceof FunctionTask) {
			return Position.N;
		} else {
			return super.getLabelPosition(node);
		}
	}

	@Override
	public boolean drawEdge(Edge edge) {
		if (edge instanceof FunctionDependency) {
			return false;
		} else {
			return super.drawEdge(edge);
		}
	}

}
