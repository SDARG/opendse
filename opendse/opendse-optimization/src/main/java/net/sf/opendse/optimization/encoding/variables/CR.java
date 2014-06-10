package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CR extends Variable implements CommunicationVariable {

	public CR(Task c, Resource r) {
		super(c,r);
	}

	public Task getC() {
		return get(0);
	}

	public Resource getR() {
		return get(1);
	}

	@Override
	public ICommunication getCommunication() {
		return (ICommunication)getC();
	}
}