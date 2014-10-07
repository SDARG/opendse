package net.sf.opendse.realtime.et.qcqp;

import static net.sf.opendse.model.Models.isCommunication;
import static net.sf.opendse.model.Models.isProcess;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.graph.TimingElement;
import net.sf.opendse.realtime.et.graph.TimingGraph;

public class MyTimingPropertyAnnotater {

	public void annotate(TimingGraph tg, Specification implementation) {
		annotate(tg, implementation, "");
	}

	public void annotate(TimingGraph tg, Specification implementation, String postfix) {

		for (TimingElement te : tg) {
			Task task = te.getTask();

			Node node = null;

			if (isProcess(task)) {
				node = task;
			} else if (isCommunication(task)) {
				node = implementation.getRoutings().get(task).getVertex(te.getResource());
			}

			// Resource resource = te.getResource();

			Double executionTime = te.getAttribute("e");
			Double jitter = te.getAttribute("jitter[in]");
			Double delay = te.getAttribute("delay");
			Double responseTime = te.getAttribute("response");

			//node.setAttribute("jitter[in]" + postfix, jitter);
			node.setAttribute("jitter" + postfix, adjust(jitter + (responseTime - executionTime)));
			node.setAttribute("delay" + postfix, delay);
			node.setAttribute("response" + postfix, responseTime);
		}

	}

	public static double adjust(double value) {
		return Math.round(value * 100000.0) / 100000.0;
	}

}
