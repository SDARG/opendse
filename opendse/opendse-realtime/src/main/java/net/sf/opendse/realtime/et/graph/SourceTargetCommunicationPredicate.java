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

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

import org.apache.commons.collections15.Predicate;

public class SourceTargetCommunicationPredicate implements Predicate<TimingElement> {

	protected Specification specification;
	protected TimingGraph tg;
	
	public SourceTargetCommunicationPredicate(Specification specification, TimingGraph tg){
		this.specification = specification;
		this.tg = tg;
	}

	@Override
	public boolean evaluate(TimingElement timingElement) {
		
		Task task = timingElement.getTask();
		Resource resource = timingElement.getResource();
		
		Routings<Task, Resource, Link> routings = specification.getRoutings();
		
		//System.out.println(timingElement+" "+tg.getPredecessors(timingElement)+" "+tg.getSuccessors(timingElement));
		
		if(Models.isCommunication(task)){
			Architecture<Resource, Link> routing = routings.get(task);
			//System.out.println(task+" "+routing+" "+resource+" "+routing.getInEdges(resource));
			int inCount = routing.getInEdges(resource).size();
			int outCount = routing.getOutEdges(resource).size();
			
			return inCount > 0 && outCount > 0;			
		} else {
			return true;
		}
		
	}

}
