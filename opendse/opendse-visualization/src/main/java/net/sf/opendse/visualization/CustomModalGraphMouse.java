package net.sf.opendse.visualization;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.ItemSelectable;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class CustomModalGraphMouse<V, E> extends AbstractModalGraphMouse implements ModalGraphMouse, ItemSelectable,
		MouseListener, MouseMotionListener {

	public CustomModalGraphMouse() {
		this(1.1f, 1.0f / 1.1f);
	}

	protected CustomModalGraphMouse(float in, float out) {
		super(in, out);
		loadPlugins();
	}

	@Override
	protected void loadPlugins() {
		//pickingPlugin = new PickingGraphMousePlugin<V, E>();
		//animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<V, E>();
		translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
		scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
		rotatingPlugin = new RotatingGraphMousePlugin();
		shearingPlugin = new ShearingGraphMousePlugin();

		add(scalingPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);

		setMode(Mode.TRANSFORMING);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseMoved(MouseEvent e) {
		VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) e.getSource();
		GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
		Layout<V, E> layout = vv.getGraphLayout();
		Point2D ip = e.getPoint();

		V vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
		E edge = null; // pickSupport.getEdge(layout, ip.getX(), ip.getY());

		final Cursor cursor;
		if (vertex != null || edge != null) {
			cursor = new Cursor(Cursor.HAND_CURSOR);
		} else {
			cursor = new Cursor(Cursor.DEFAULT_CURSOR);
		}
		((Component) e.getSource()).setCursor(cursor);
	}

}
