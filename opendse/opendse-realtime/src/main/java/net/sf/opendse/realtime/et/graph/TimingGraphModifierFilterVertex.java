package net.sf.opendse.realtime.et.graph;

import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Specification;

import org.apache.commons.collections15.Predicate;

public class TimingGraphModifierFilterVertex implements TimingGraphModifier {

	protected Predicate<TimingElement> removePredicate;
	
	public TimingGraphModifierFilterVertex(Predicate<TimingElement> removePredicate){
		this.removePredicate = removePredicate;
	}
	
	@Override
	public void apply(Specification implementation, TimingGraph timingGraph) {
		
		
		Set<TimingElement> remove = new HashSet<TimingElement>();
		
		for(TimingElement timingElement: timingGraph.getVertices()){
			if(!removePredicate.evaluate(timingElement)){
				remove.add(timingElement);
			}
		}
		
		for(TimingElement timingElement: remove){
			
			Set<TimingElement> incoming = new HashSet<TimingElement>();
			Set<TimingElement> outgoing = new HashSet<TimingElement>();
			
			for(TimingDependency inTimingDependency: timingGraph.getInEdges(timingElement)){
				if(inTimingDependency instanceof TimingDependencyTrigger){
					incoming.add(timingGraph.getOpposite(timingElement, inTimingDependency));
				}
			}
			
			for(TimingDependency outTimingDependency: timingGraph.getOutEdges(timingElement)){
				if(outTimingDependency instanceof TimingDependencyTrigger){
					outgoing.add(timingGraph.getOpposite(timingElement, outTimingDependency));
				}
			}
			
			timingGraph.removeVertex(timingElement);
			
			for(TimingElement source: incoming){
				for(TimingElement target: outgoing){
					timingGraph.addEdge(new TimingDependencyTrigger(), source, target);
				}
			}
		}
		
		

	}

}
