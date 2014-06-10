package net.sf.opendse.visualization;

import static net.sf.opendse.visualization.Graphics.mix;
import static net.sf.opendse.visualization.Graphics.tone;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.sf.opendse.model.Architecture;
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
import net.sf.opendse.visualization.GraphPanel.LocalEdge;
import net.sf.opendse.visualization.GraphPanelFormatApplication.FunctionTask;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.util.Pair;

public class GraphPanelFormatArchitecture extends AbstractGraphPanelFormat {

	protected final Specification specification;
	protected final Architecture<Resource, Link> architecture;
	protected final Mappings<Task, Resource> mappings;
	protected final Routings<Task, Resource, Link> routings;
	protected final ElementSelection selection;

	public GraphPanelFormatArchitecture(Specification specification, ElementSelection selection) {
		super();
		this.specification = specification;
		this.architecture = specification.getArchitecture();
		this.mappings = specification.getMappings();
		this.routings = specification.getRoutings();
		this.selection = selection;
	}

	@SuppressWarnings("unchecked")
	public Graph<Node, Edge> getGraph() {
		Graph<?, ?> g = architecture;
		return (Graph<Node, Edge>) g;
	}

	@Override
	public Layout<Node, LocalEdge> getLayout(DirectedGraph<Node, LocalEdge> graph) {
		FRLayout<Node, LocalEdge> layout = new FRLayout<Node, LocalEdge>(graph);
		layout.setSize(new Dimension(600, 600));
		layout.setAttractionMultiplier(5);
		layout.setRepulsionMultiplier(0.1);

		final Dimension size = layout.getSize();
		final Random random = new Random(0);

		layout.setInitializer(new Transformer<Node, Point2D>() {
			@Override
			public Point2D transform(Node arg0) {
				return new Point2D.Double(size.getWidth() / 2 + random.nextDouble(), size.getHeight() / 2
						+ random.nextDouble());
			}
		});
		return layout;
	}

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
	public Color getColor(Node node) {
		Color c = colors.get(node.getType());
		if (c != null) {
			return c;
		} else {
			return Graphics.STEELBLUE;
		}
	}

	protected Map<FunctionTask, Set<Resource>> targets = new HashMap<FunctionTask, Set<Resource>>();

	@Override
	public boolean isActive(Node node) {
		if (selection.isNull() || selection.isSelected(node)) {
			return true;
		} else if (selection.get() instanceof FunctionTask) {
			if (targets.containsKey(selection.get())) {
				return targets.get(selection.get()).contains((Resource) node);
			} else {
				Function<Task, Dependency> function = ((FunctionTask) selection.get()).getFunction();
				Set<Resource> ts = new HashSet<Resource>();
				for (Task t : Models.filterProcesses(function)) {
					ts.addAll(mappings.getTargets(t));
				}
				for (Task t: Models.filterCommunications(function)){
					ts.addAll(routings.get(t).getVertices());
				}
				targets.put((FunctionTask)selection.get(), ts);
				return ts.contains((Resource) node);
			}
		} else if (selection.get() instanceof Task) {
			Task task = selection.get();
			if (selection.get() instanceof ICommunication) {
				return routings.get(task).containsVertex((Resource) node);
			} else {
				return mappings.getTargets(task).contains(node);
			}
		} else if (selection.get() instanceof Mapping<?, ?>) {
			Mapping<Task, Resource> m = selection.get();
			return node.equals(m.getTarget());
		}
		return false;
	}

	@Override
	public boolean isActive(Edge edge, Node n0, Node n1) {
		if (!selection.isNull() && selection.get() instanceof ICommunication) {
			Architecture<Resource, Link> routing = routings.get((Task) selection.get());
			if (routing.findEdge((Resource) n0, (Resource) n1) != null) {
				return true;
			} else {
				return false;
			}
		} else {
			Pair<Resource> endpoints = architecture.getEndpoints((Link) edge);
			Resource r0 = endpoints.getFirst();
			Resource r1 = endpoints.getSecond();
			return isActive(r0) && isActive(r1);
		}
	}

	@Override
	public Color getColor(Edge edge) {
		Pair<Resource> endpoints = architecture.getEndpoints((Link) edge);
		Resource r0 = endpoints.getFirst();
		Resource r1 = endpoints.getSecond();
		return tone(mix(getColor(r0), getColor(r1)), -0.5);
	}

	@Override
	public String getTooltip(Node node) {
		if (!selection.isNull() && selection.get() instanceof ICommunication) {
			Architecture<Resource, Link> routing = routings.get((Task) selection.get());
			return super.getTooltip(routing.getVertex((Resource) node));
		} else {
			return super.getTooltip(node);
		}
	}

	@Override
	public String getTooltip(Edge edge) {
		if (!selection.isNull() && selection.get() instanceof ICommunication) {
			Architecture<Resource, Link> routing = routings.get((Task) selection.get());
			return super.getTooltip(routing.getEdge((Link) edge));
		} else {
			return super.getTooltip(edge);
		}
	}

	@Override
	public Shape getShape(Node node) {
		return shapes.getRoundRectangle(node);
	}

	@Override
	public int getSize(Node node) {
		return 20;
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
}
