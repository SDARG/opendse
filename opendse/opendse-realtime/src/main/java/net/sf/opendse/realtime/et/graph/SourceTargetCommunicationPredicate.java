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
