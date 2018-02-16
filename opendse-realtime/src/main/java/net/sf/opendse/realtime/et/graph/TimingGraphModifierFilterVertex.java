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
