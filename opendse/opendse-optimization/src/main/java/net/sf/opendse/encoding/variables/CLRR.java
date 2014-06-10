package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CLRR extends Variable implements CommunicationVariable {

	public CLRR(Task t, Edge l, Resource r0, Resource r1) {
		super(t,l,r0,r1);
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
	
	@Override
	public ICommunication getCommunication() {
		return (ICommunication)getTask();
	}
}
