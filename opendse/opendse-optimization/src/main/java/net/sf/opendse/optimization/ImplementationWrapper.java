package net.sf.opendse.optimization;

import net.sf.opendse.model.Specification;

public class ImplementationWrapper {

	protected Specification implementation;

	public ImplementationWrapper(Specification implementation) {
		super();
		this.implementation = implementation;
	}

	public Specification getImplementation() {
		return implementation;
	}
	
	public void setImplementation(Specification implementation){
		this.implementation = implementation;
	}
	
}
