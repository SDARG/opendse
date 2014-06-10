package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CLRRT extends Variable implements CommunicationVariable {

	public CLRRT(Task t, Edge l, Resource r0, Resource r1, int s) {
		super(t,l,r0,r1,s);
	}

	public Task getTask() {
		return get(0);
	}

	public Edge getLink() {
		return get(1);
	}

	public Resource getSource() {
		return get(2);
	}

	public Resource getDest() {
		return get(3);
	}
	
	public Integer getStep(){
		return get(4);
	}
	
	@Override
	public ICommunication getCommunication() {
		return (ICommunication)getTask();
	}
}
