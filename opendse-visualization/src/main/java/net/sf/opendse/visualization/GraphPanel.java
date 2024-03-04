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

import static edu.uci.ics.jung.graph.util.EdgeType.DIRECTED;
import static edu.uci.ics.jung.graph.util.EdgeType.UNDIRECTED;
import static net.sf.opendse.visualization.Graphics.tone;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.collections15.Bag;
import org.apache.commons.collections15.bag.HashBag;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeIndexFunction;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Graph;
import net.sf.opendse.model.Node;

public class GraphPanel extends JPanel implements ElementSelectionListener {

	private static final long serialVersionUID = 1L;

	protected Graph<Node, Edge> graph;
	protected DirectedSparseMultigraph<Node, LocalEdge> localGraph;
	protected GraphPanelFormat format;
	protected ElementSelection selection;

	protected boolean drawEdgeLabel = false;
	protected boolean drawNodeLabel = true;
	protected String labelSelection = "ID";

	class SelectionPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public SelectionPanel() {
			super(new FlowLayout(FlowLayout.LEFT));

			final JCheckBox nodeLabels = new JCheckBox("Nodes", drawNodeLabel);
			final JCheckBox edgeLabels = new JCheckBox("Edges", drawEdgeLabel);
			final JComboBox<String> labelBox = new JComboBox<String>();

			List<String> list = new ArrayList<String>(new HashSet<String>(attributesBag));
			Collections.sort(list, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					Integer i1 = attributesBag.getCount(o1);
					Integer i2 = attributesBag.getCount(o2);
					int c = i2.compareTo(i1);
					if (c == 0) {
						return o1.compareTo(o2);
					} else {
						return c;
					}
				}
			});
			list.add(0, "ID");
			for (String s : list) {
				labelBox.addItem(s);
			}

			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					drawNodeLabel = nodeLabels.isSelected();
					drawEdgeLabel = edgeLabels.isSelected();
					labelSelection = (String) labelBox.getSelectedItem();
					GraphPanel.this.repaint();
				}
			};

			nodeLabels.addActionListener(listener);
			edgeLabels.addActionListener(listener);
			labelBox.addActionListener(listener);

			setBackground(Color.WHITE);
			nodeLabels.setBackground(Color.WHITE);
			edgeLabels.setBackground(Color.WHITE);
			add(new JLabel("Show labels: "));
			add(nodeLabels);
			add(edgeLabels);
			labelBox.setBackground(Color.WHITE);
			add(labelBox);
		}
	}

	public class CustomVertexLabelRenderer extends DefaultVertexLabelRenderer {

		private static final long serialVersionUID = 1L;

		public CustomVertexLabelRenderer() {
			super(Color.BLACK);
		}

		@Override
		public <V> Component getVertexLabelRendererComponent(JComponent vv, Object value, Font font, boolean isSelected,
				V vertex) {
			Component comp = super.getVertexLabelRendererComponent(vv, value, font, isSelected, vertex);
			comp.setBackground(new Color(0x77FFFFFF, true));

			if (isActive((Node) vertex)) {
				comp.setForeground(Color.BLACK);
			} else {
				comp.setForeground(Graphics.LIGHTGRAY);
			}

			setValue(value + " ");
			return this;
		}
	};

	public class CustomVertexRenderer extends BasicVertexRenderer<Node, LocalEdge> {

		AffineTransform transform = AffineTransform.getTranslateInstance(3, 3);
		Color shadowColor = Graphics.alpha(Color.BLACK, 0.2);

		@Override
		protected void paintShapeForVertex(RenderContext<Node, LocalEdge> rc, Node node, Shape shape) {
			GraphicsDecorator g = rc.getGraphicsContext();

			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Shape shadow = transform.createTransformedShape(shape);
			g.setPaint(shadowColor);
			g.fill(shadow);

			super.paintShapeForVertex(rc, node, shape);
			// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_OFF);

			Shape symbol = format.getSymbol(node);
			Paint drawPaint = rc.getVertexDrawPaintTransformer().apply(node);

			if (symbol != null && drawPaint != null) {
				Paint tmpPaint = g.getPaint();
				g.setPaint(drawPaint);
				Rectangle2D bounds = shape.getBounds2D();
				double x = (bounds.getMaxX() + bounds.getMinX()) / 2;
				double y = (bounds.getMaxY() + bounds.getMinY()) / 2;
				g.translate(x, y);
				g.fill(symbol);
				g.setPaint(Graphics.tone(getColor(node), -0.75));
				g.draw(symbol);

				g.translate(-x, -y);
				g.setPaint(tmpPaint);
			}
			// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			// RenderingHints.VALUE_ANTIALIAS_ON);

		}
	}

	public class CustomEdgeLabelRender extends DefaultEdgeLabelRenderer {

		private static final long serialVersionUID = 1L;

		public CustomEdgeLabelRender() {
			super(Color.BLACK);
		}

		@Override
		public <E> Component getEdgeLabelRendererComponent(JComponent vv, Object value, Font font, boolean isSelected,
				E edge) {
			Component comp = super.getEdgeLabelRendererComponent(vv, value, font, isSelected, edge);
			comp.setBackground(new Color(0x77FFFFFF, true));

			LocalEdge e = (LocalEdge) edge;

			if (isActive(e.getEdge(), e.getSource(), e.getDest())) {
				comp.setForeground(Color.BLACK);
			} else {
				comp.setForeground(Graphics.LIGHTGRAY);
			}

			setValue(value);
			return this;
		}
	}

	Bag<String> attributesBag = new HashBag<String>();

	public GraphPanel(GraphPanelFormat format, ElementSelection selection) {
		this.graph = format.getGraph();
		for (Node node : this.graph.getVertices()) {
			attributesBag.addAll(node.getAttributeNames());
		}
		for (Edge edge : this.graph.getEdges()) {
			attributesBag.addAll(edge.getAttributeNames());
		}

		this.localGraph = new DirectedSparseMultigraph<Node, LocalEdge>();

		for (Node node : this.graph.getVertices()) {
			localGraph.addVertex(node);
		}
		for (Object edge : this.graph.getEdges()) {
			Edge e = (Edge) edge;

			if (this.graph.getEdgeType(e) == UNDIRECTED) {
				Pair<Node> endpoints = this.graph.getEndpoints(e);
				Node n0 = endpoints.getFirst();
				Node n1 = endpoints.getSecond();
				if (n0.getId().compareTo(n1.getId()) > 0) {
					n0 = endpoints.getFirst();
					n1 = endpoints.getSecond();
				} else {
					n0 = endpoints.getSecond();
					n1 = endpoints.getFirst();
				}

				LocalEdge e0 = new LocalEdge(e, n0, n1, UNDIRECTED);
				LocalEdge e1 = new LocalEdge(e, n1, n0, UNDIRECTED);
				localGraph.addEdge(e0, n0, n1);
				localGraph.addEdge(e1, n1, n0);
			} else { // DIRECTED
				Node source = this.graph.getSource(e);
				Node dest = this.graph.getDest(e);
				LocalEdge e0 = new LocalEdge(e, source, dest, DIRECTED);
				localGraph.addEdge(e0, source, dest);
			}
		}

		// this.graph = (Graph<Node, Edge>) graph;
		this.format = format;
		this.selection = selection;
		this.selection.addListener(this);

		init();
	}

	protected Color getColor(Node node) {
		Color color = format.getColor(node);
		if (!isActive(node)) {
			color = Graphics.tone(color, 3);
		}
		return color;
	}

	protected Color getColor(Edge edge, Node n0, Node n1) {
		Color color = format.getColor(edge);
		color = Graphics.tone(color, -4.5);
		if (!isActive(edge, n0, n1)) {
			color = Graphics.tone(color, 3);
		}
		return color;
	}

	protected Shape getShape(Node node) {
		return format.getShape(node);
	}

	protected String getLabel(Node node) {
		if (!drawNodeLabel) {
			return "";
		} else if ("ID".equals(labelSelection)) {
			return node.getId();
		} else {
			Object v = node.getAttribute(labelSelection);
			return v != null ? ViewUtil.objectToString(v) : "";
		}
	}

	protected String getLabel(Edge edge) {
		if (!drawEdgeLabel) {
			return "";
		} else if ("ID".equals(labelSelection)) {
			return edge.getId();
		} else {
			Object v = edge.getAttribute(labelSelection);
			return v != null ? ViewUtil.objectToString(v) : "";
		}
	}

	protected boolean isActive(Node node) {
		return format.isActive(node);
	}

	protected boolean isActive(Edge edge, Node n0, Node n1) {
		return format.isActive(edge, n0, n1);
	}

	protected boolean isArrow(LocalEdge edge) {
		if (edge.getType() == DIRECTED) {
			return true;
		} else {
			LocalEdge opp = null;

			for (LocalEdge oppc : localGraph.findEdgeSet(edge.getDest(), edge.getSource())) {
				if (edge.getEdge().equals(oppc.getEdge())) {
					opp = oppc;
					break;
				}
			}

			boolean v0 = isVisible(edge);
			boolean v1 = isVisible(opp);

			if (v0 == v1) {
				return false;
			} else {
				return v0;
			}
		}
	}

	protected boolean isVisible(LocalEdge edge) {
		if (format.drawEdge(edge.getEdge()) == false) {
			return false;
		}
		if (edge.getType() == DIRECTED) {
			return true;
		} else {
			if (isActive(edge.getEdge(), edge.getSource(), edge.getDest())) {
				return true;
			}
			if (isActive(edge.getEdge(), edge.getDest(), edge.getSource())) {
				return false;
			}
			return true;
		}
	}

	protected void init() {
		setLayout(new BorderLayout());

		final Layout<Node, LocalEdge> layout = format.getLayout(localGraph);
		final VisualizationModel<Node, LocalEdge> vm = new DefaultVisualizationModel<Node, LocalEdge>(layout);
		final VisualizationViewer<Node, LocalEdge> vv = new VisualizationViewer<Node, LocalEdge>(vm);

		vv.setBackground(Color.WHITE);

		RenderContext<Node, LocalEdge> ctx = vv.getRenderContext();

		ctx.setVertexFillPaintTransformer(new Function<Node, Paint>() {
			@Override
			public Paint apply(Node node) {
				double size = format.getSize(node);
				Point2D point = layout.apply(node);
				point = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, point);
				Point2D p1 = new Point2D.Double(point.getX(), point.getY() - size / 2);
				Point2D p2 = new Point2D.Double(point.getX(), point.getY() + size / 2);
				GradientPaint gp = new GradientPaint(p1, getColor(node), p2, Graphics.WHITE);
				return gp;
			}
		});
		ctx.setVertexDrawPaintTransformer(new Function<Node, Paint>() {
			@Override
			public Paint apply(Node node) {
				return tone(getColor(node), -4.5);
			}
		});
		vv.setVertexToolTipTransformer(new Function<Node, String>() {
			@Override
			public String apply(Node node) {
				return format.getTooltip(node);
			}
		});
		vv.setEdgeToolTipTransformer(new Function<LocalEdge, String>() {
			@Override
			public String apply(LocalEdge edge) {
				return format.getTooltip(edge.getEdge());
			}
		});

		Function<LocalEdge, Paint> edgePaint = new Function<LocalEdge, Paint>() {
			@Override
			public Paint apply(LocalEdge edge) {
				return getColor(edge.getEdge(), edge.getSource(), edge.getDest());
			}
		};
		ctx.setEdgeDrawPaintTransformer(edgePaint);
		ctx.setArrowDrawPaintTransformer(edgePaint);
		ctx.setArrowFillPaintTransformer(edgePaint);

		final Stroke THIN = new BasicStroke(1.0f);
		ctx.setEdgeStrokeTransformer(new Function<LocalEdge, Stroke>() {
			@Override
			public Stroke apply(LocalEdge edge) {
				return THIN;
			}
		});
		ctx.setVertexStrokeTransformer(new Function<Node, Stroke>() {
			@Override
			public Stroke apply(Node node) {
				return THIN;
			}
		});

		ctx.setVertexShapeTransformer(new Function<Node, Shape>() {
			@Override
			public Shape apply(Node node) {
				return getShape(node);
			}
		});

		final EdgeIndexFunction<Node, LocalEdge> edgeIndexFunction = new EdgeIndexFunction<Node, LocalEdge>() {

			Map<LocalEdge, Integer> values = new HashMap<LocalEdge, Integer>();

			@Override
			public void reset(edu.uci.ics.jung.graph.Graph<Node, LocalEdge> g, LocalEdge edge) {
				values.remove(edge);
			}

			@Override
			public void reset() {
				values.clear();
			}

			@Override
			public int getIndex(edu.uci.ics.jung.graph.Graph<Node, LocalEdge> graph, LocalEdge e) {
				if (values.containsKey(e)) {
					return values.get(e);
				}

				Pair<Node> endpoints = graph.getEndpoints(e);
				Set<Edge> allEdges = new HashSet<Edge>();
				for (LocalEdge localEdge : graph.findEdgeSet(endpoints.getFirst(), endpoints.getSecond())) {
					allEdges.add(localEdge.getEdge());
				}
				for (LocalEdge localEdge : graph.findEdgeSet(endpoints.getSecond(), endpoints.getFirst())) {
					allEdges.add(localEdge.getEdge());
				}
				List<Edge> listEdges = new ArrayList<Edge>(allEdges);
				Collections.sort(listEdges, new Comparator<Edge>() {
					@Override
					public int compare(Edge o1, Edge o2) {
						return o1.getId().compareTo(o2.getId());
					}
				});

				return listEdges.indexOf(e.getEdge())
						* (endpoints.getFirst().getId().compareTo(endpoints.getSecond().getId()) > 0 ? 1 : -1);
			}
		};

		
		EdgeShape<Node,LocalEdge> edgeShape = new EdgeShape<Node,LocalEdge>((edu.uci.ics.jung.graph.Graph<Node, LocalEdge>)localGraph);
		
		final Function<LocalEdge, Shape> curve = edgeShape.new QuadCurve() {
			private final QuadCurve2D instance = new QuadCurve2D.Float();
			
			@Override
			public Shape apply(LocalEdge e) {
				Pair<Node> endpoints = ((edu.uci.ics.jung.graph.Graph<Node, LocalEdge>)localGraph).getEndpoints(e);
				if (endpoints != null) {
					boolean isLoop = endpoints.getFirst().equals(endpoints.getSecond());
					if (isLoop) {
						return super.apply(e);
					}
				}

				int index = 1;
				if (edgeIndexFunction != null) {
					index = edgeIndexFunction.getIndex((edu.uci.ics.jung.graph.Graph<Node, LocalEdge>)localGraph, e);
				}

				float controlY = control_offset_increment * index;

				instance.setCurve(0.0f, 0.0f, 0.5f, controlY, 1.0f, 0.0f);
				return instance;
			}
		};
		
		((EdgeShape<Node, LocalEdge>.QuadCurve) curve).setEdgeIndexFunction(edgeIndexFunction);

		ctx.setEdgeShapeTransformer(curve);
		
		ctx.setVertexLabelTransformer(new Function<Node, String>() {
			@Override
			public String apply(Node node) {
				return getLabel(node);
			}
		});
		ctx.setEdgeLabelTransformer(new Function<LocalEdge, String>() {
			@Override
			public String apply(LocalEdge edge) {
				return getLabel(edge.getEdge());
			}
		});
		ctx.setEdgeArrowPredicate(new Predicate<Context<edu.uci.ics.jung.graph.Graph<Node, LocalEdge>, LocalEdge>>() {

			@Override
			public boolean apply(Context<edu.uci.ics.jung.graph.Graph<Node, LocalEdge>, LocalEdge> ctx) {
				LocalEdge edge = ctx.element;
				return isArrow(edge);
			}
		});
		ctx.setEdgeIncludePredicate(new Predicate<Context<edu.uci.ics.jung.graph.Graph<Node, LocalEdge>, LocalEdge>>() {

			@Override
			public boolean apply(Context<edu.uci.ics.jung.graph.Graph<Node, LocalEdge>, LocalEdge> ctx) {
				LocalEdge edge = ctx.element;
				return isVisible(edge);
			}
		});
		
		ctx.setVertexLabelRenderer(new CustomVertexLabelRenderer());
		ctx.setEdgeLabelRenderer(new CustomEdgeLabelRender());
		vv.getRenderer().setVertexRenderer(new CustomVertexRenderer());
		vv.getRenderer().setVertexLabelRenderer(new BasicVertexLabelRenderer<Node, LocalEdge>() {
			@Override
			public void labelVertex(RenderContext<Node, LocalEdge> arg0, Layout<Node, LocalEdge> arg1, Node arg2,
					String arg3) {
				position = format.getLabelPosition(arg2);
				super.labelVertex(arg0, arg1, arg2, arg3);
			}
		});
		ctx.setEdgeArrowTransformer(
				new Function<Context<edu.uci.ics.jung.graph.Graph<Node, LocalEdge>, LocalEdge>, Shape>() {
					@Override
					public Shape apply(Context<edu.uci.ics.jung.graph.Graph<Node, LocalEdge>, LocalEdge> arg0) {
						return ArrowFactory.getWedgeArrow(8, 5);
					}
				});

		
		
		

		vv.addGraphMouseListener(new GraphMouseListener<Node>() {
			@Override
			public void graphClicked(Node node, MouseEvent me) {
				if (me.getClickCount() == 1) {
					if (selection.get() == node) {
						selection.set(null);
					} else {
						selection.set(node);
					}
				}
			}
			

			@Override
			public void graphPressed(Node node, MouseEvent me) {
			}

			@Override
			public void graphReleased(Node node, MouseEvent me) {
			}
		});
		

		JPanel panel = new JPanel(new BorderLayout());

		GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
		panel.add(BorderLayout.NORTH, new SelectionPanel());
		panel.add(BorderLayout.CENTER, scrollPane);
		add(panel);
		
		ModalGraphMouse mouse = new DefaultModalGraphMouse<Node, Edge>();
		mouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		vv.setGraphMouse(mouse);
		
	}

	@Override
	public void selectionChanged(ElementSelection selection) {
		repaint();
	}
	
}
