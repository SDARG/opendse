package net.sf.opendse.realtime.et.qcqp;

import static net.sf.opendse.model.Models.isCommunication;
import static net.sf.opendse.model.Models.isProcess;
import static net.sf.opendse.realtime.et.PriorityScheduler.DELAY;
import static net.sf.opendse.realtime.et.PriorityScheduler.JITTER;
import static net.sf.opendse.realtime.et.PriorityScheduler.RESPONSE;
import net.sf.opendse.model.Node;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.PriorityScheduler;
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
			Double jitterIn = te.getAttribute("jitter[in]");
			Double jitterOut = te.getAttribute("jitter[out]");
			Double delay = te.getAttribute("delay");
			Double responseTime = te.getAttribute("response");

			//node.setAttribute("jitter[in]" + postfix, jitter);
			node.setAttribute(JITTER + postfix, jitterOut);
			node.setAttribute(JITTER+"[in]" + postfix, jitterIn);
			node.setAttribute(JITTER+"[out]" + postfix, jitterOut);
			node.setAttribute(DELAY + postfix, delay);
			node.setAttribute(RESPONSE + postfix, responseTime); 
		}

	}

	public static double adjust(double value) {
		return Math.round(value * 100000.0) / 100000.0;
	}

}
