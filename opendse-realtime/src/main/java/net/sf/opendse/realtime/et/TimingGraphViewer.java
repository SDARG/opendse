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
package net.sf.opendse.realtime.et;

import java.awt.Dimension;
import java.awt.Paint;

import javax.swing.JFrame;

import net.sf.opendse.realtime.et.graph.TimingDependency;
import net.sf.opendse.realtime.et.graph.TimingDependencyTrigger;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;
import net.sf.opendse.visualization.Graphics;
import net.sf.opendse.visualization.algorithm.DistanceFlowLayout;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

public class TimingGraphViewer {

	public static void view(TimingGraph tg) {
		
		/*TimingGraph tgFilter = FilterUtils.createInducedSubgraph(tg.getVertices(), tg);
		for(TimingDependency td: tg.getEdges()){
			if(td instanceof TimingDependencyPriority){
				tgFilter.removeEdge(td);
			}
		}*/
		
		Layout<TimingElement, TimingDependency> layout = new DistanceFlowLayout<TimingElement, TimingDependency>(tg);

		VisualizationModel<TimingElement, TimingDependency> vm = new DefaultVisualizationModel<TimingElement, TimingDependency>(layout,
				new Dimension(800, 600));
		VisualizationViewer<TimingElement, TimingDependency> vv = new VisualizationViewer<TimingElement, TimingDependency>(vm);

		DefaultModalGraphMouse<TimingElement, TimingDependency> graphMouse = new DefaultModalGraphMouse<TimingElement, TimingDependency>();
		vv.setGraphMouse(graphMouse);

		RenderContext<TimingElement, TimingDependency> ctx = vv.getRenderContext();

		ctx.setVertexLabelTransformer(new Transformer<TimingElement, String>() {
			public String transform(TimingElement timingEntity) {
				return timingEntity.toString();
			}
		});

		ctx.setVertexFillPaintTransformer(new Transformer<TimingElement, Paint>() {
			@Override
			public Paint transform(TimingElement entity) {
				//if (entity.isCommunication()) {
					return Graphics.ALICEBLUE;
				//} else {
				//	return Graphics.ROSYBROWN;
				//}
			}
		});
		
		ctx.setEdgeDrawPaintTransformer(new Transformer<TimingDependency, Paint>() {
			@Override
			public Paint transform(TimingDependency td) {
				if(td instanceof TimingDependencyTrigger){
					return Graphics.BLACK;
				} else {
					return Graphics.BLUE;
				}
			}
			
		});

		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		JFrame frame = new JFrame("TimingEntity Graph");
		frame.add(panel);

		frame.pack();
		frame.setVisible(true);

	}

	protected static class VertexSizeTransformer implements Transformer<TimingElement, Integer> {
		@Override
		public Integer transform(TimingElement vertex) {
			return 10;
		}
	}

	protected static class VertexRatioTransformer implements Transformer<TimingElement, Float> {
		@Override
		public Float transform(TimingElement vertex) {
			return 1.0f;
		}
	}

}