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
package net.sf.opendse.realtime.et.qcqp;

import static net.sf.opendse.model.Models.isCommunication;
import static net.sf.opendse.model.Models.isProcess;
import static net.sf.opendse.realtime.et.PriorityScheduler.DELAY;
import static net.sf.opendse.realtime.et.PriorityScheduler.JITTER;
import static net.sf.opendse.realtime.et.PriorityScheduler.RESPONSE;

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

			Double jitterOut = te.getAttribute("jitter[out]");
			Double delay = te.getAttribute("delay");
			Double responseTime = te.getAttribute("response");

			//node.setAttribute("jitter[in]" + postfix, jitter);
			node.setAttribute(JITTER + postfix, jitterOut);
			//node.setAttribute(JITTER+"[in]" + postfix, jitterIn);
			//node.setAttribute(JITTER+"[out]" + postfix, jitterOut);
			node.setAttribute(DELAY + postfix, delay);
			node.setAttribute(RESPONSE + postfix, responseTime); 
		}

	}

	public static double adjust(double value) {
		return Math.round(value * 100000.0) / 100000.0;
	}

}
