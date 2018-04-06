package net.sf.opendse.encoding.variables;

import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The M variable encodes the activation of a {@link Mapping}.
 * 
 * @author Fedor Smirnov
 *
 */
public class M extends Variable implements MappingVariable {

	protected M(Mapping<Task, Resource> mapping) {
		super(mapping);
	}
	
	public Mapping<Task, Resource> getMapping(){
		return get(0);
	}
}
