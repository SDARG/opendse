package net.sf.opendse.visualization;

import java.awt.Polygon;
import java.awt.Shape;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Node;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

public abstract class AbstractGraphPanelFormat implements GraphPanelFormat {

	@Override
	public String getTooltip(Node node) {
		return ViewUtil.getTooltip(node);
	}

	@Override
	public String getTooltip(Edge edge) {
		return ViewUtil.getTooltip(edge);
	}
	
	protected class CustomVertexShapeFactory extends VertexShapeFactory<Node> {

		public CustomVertexShapeFactory() {
			super(new Transformer<Node, Integer>() {
				@Override
				public Integer transform(Node node) {
					return getSize(node);
				}

			}, new Transformer<Node, Float>() {
				public Float transform(Node arg0) {
					return 1.0f;
				}
			});
		}
		
		public Shape getInnerOut(Node node){
			int s = vsf.transform(node)/2;
			Polygon poly = new Polygon();	
			poly.addPoint(s/2, 0);
			poly.addPoint(-s/3, -2*s/3);
			poly.addPoint(-s/3, 2*s/3);
			return poly;
		}
		
		public Shape getInnerIn(Node node){
			int s = vsf.transform(node)/4;
			Polygon poly = new Polygon();
			poly.addPoint(0, 0);
			poly.addPoint(-s, -s);
			poly.addPoint(s, -s);
			poly.addPoint(s, s);
			poly.addPoint(-s, s);
			return poly;
		}

	}

	protected final CustomVertexShapeFactory shapes = new CustomVertexShapeFactory();

	@Override
	public Shape getSymbol(Node node) {
		return null;
	}

	@Override
	public Position getLabelPosition(Node node) {
		return Position.E;
	}

	@Override
	public boolean drawEdge(Edge edge) {
		return true;
	}
	
}
