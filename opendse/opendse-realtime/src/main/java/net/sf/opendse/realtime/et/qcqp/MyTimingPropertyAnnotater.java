package net.sf.opendse.realtime.et.qcqp;

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
			// Resource resource = te.getResource();

			Double executionTime = te.getAttribute("e");
			Double jitter = te.getAttribute("jitter[in]");
			Double delay = te.getAttribute("delay");
			Double responseTime = te.getAttribute("response");

			task.setAttribute("jitter[in]" + postfix, jitter);
			task.setAttribute("jitter[out]" + postfix, adjust(jitter + (responseTime - executionTime)));
			task.setAttribute("delay" + postfix, delay);
			task.setAttribute("response" + postfix, responseTime);
		}

	}

	public static double adjust(double value) {
		return Math.round(value * 100000.0) / 100000.0;
	}

}
