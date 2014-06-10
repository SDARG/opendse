package net.sf.opendse.model;

import java.util.Collection;
import java.util.Set;

import net.sf.opendse.model.parameter.Parameter;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class Function<T extends Task, D extends Dependency> extends Graph<T, D> implements IAttributes {

	private static final long serialVersionUID = 1L;

	protected final Attributes attributes;

	public Function(String id) {
		this(new Attributes());
		attributes.setAttribute("ID", id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Function other = (Function) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public Function(Attributes attributes) {
		super();
		this.attributes = attributes;
	}

	public String getId() {
		return getAttribute("ID");
	}

	@Override
	public void setAttribute(String identifier, Object object) {
		attributes.setAttribute(identifier, object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> O getAttribute(String identifier) {
		return (O) attributes.getAttribute(identifier);
	}
	
	

	@Override
	public Parameter getAttributeParameter(String identifier) {
		return attributes.getAttributeParameter(identifier);
	}

	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	@Override
	public Set<String> getAttributeNames() {
		return attributes.getAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * java.util.Collection)
	 */
	@Override
	public boolean addEdge(D dependency, Collection<? extends T> vertices) {
		return super.addEdge(dependency, vertices, EdgeType.DIRECTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * edu.uci.ics.jung.graph.util.Pair)
	 */
	@Override
	public boolean addEdge(D dependency, Pair<? extends T> endpoints) {
		return super.addEdge(dependency, endpoints, EdgeType.DIRECTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.AbstractGraph#addEdge(java.lang.Object,
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean addEdge(D dependency, T v1, T v2) {
		return super.addEdge(dependency, v1, v2, EdgeType.DIRECTED);
	}

	/* (non-Javadoc)
	 * @see net.sf.adse.model.IAttributes#isDefined(java.lang.String)
	 */
	@Override
	public boolean isDefined(String identifier) {
		return attributes.isDefined(identifier);
	}

}
