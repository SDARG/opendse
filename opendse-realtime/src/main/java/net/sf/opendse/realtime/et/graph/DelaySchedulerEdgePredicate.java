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
package net.sf.opendse.realtime.et.graph;

import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_NONPREEMPTIVE;
import static net.sf.opendse.realtime.et.PriorityScheduler.FIXEDPRIORITY_PREEMPTIVE;
import net.sf.opendse.model.Resource;

import org.apache.commons.collections15.Predicate;

public class DelaySchedulerEdgePredicate implements Predicate<TimingDependency> {

	protected TimingGraph timingGraph;

	public DelaySchedulerEdgePredicate(TimingGraph timingGraph) {
		this.timingGraph = timingGraph;

	}

	@Override
	public boolean evaluate(TimingDependency timingDependency) {

		if (timingDependency instanceof TimingDependencyTrigger) {
			return true;
		} else {
			TimingElement source = timingGraph.getSource(timingDependency);

			Resource resource = source.getResource();

			String scheduler = resource.getAttribute("scheduler");

			return scheduler.equals(FIXEDPRIORITY_NONPREEMPTIVE) || scheduler.equals(FIXEDPRIORITY_PREEMPTIVE);
		}

	}

}
