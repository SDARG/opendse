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
import java.awt.Polygon;
import java.awt.Shape;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Node;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

public abstract class AbstractGraphPanelFormat implements GraphPanelFormat {
	protected ColorModel colorModel = null;

	public AbstractGraphPanelFormat() {
	}

	public AbstractGraphPanelFormat(ColorModel colorModel) {
		this.colorModel = colorModel;
	}

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
				@Override
				public Float transform(Node arg0) {
					return 1.0f;
				}
			});
		}

		public Shape getInnerOut(Node node) {
			int s = vsf.transform(node) / 2;
			Polygon poly = new Polygon();
			poly.addPoint(s / 2, 0);
			poly.addPoint(-s / 3, -2 * s / 3);
			poly.addPoint(-s / 3, 2 * s / 3);
			return poly;
		}

		public Shape getInnerIn(Node node) {
			int s = vsf.transform(node) / 4;
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

	@Override
	public Color getColor(Node node) {
		if (colorModel != null) {
			Color color = colorModel.get(node);
			if (color != null) {
				return color;
			}
		}
		return Graphics.STEELBLUE;
	}

	public ColorModel getColorModel() {
		return colorModel;
	}

	public void setColorModel(ColorModel colorModel) {
		this.colorModel = colorModel;
	}

}