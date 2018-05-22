package net.sf.opendse.model;

import java.util.Set;

import net.sf.opendse.model.parameter.Parameter;

/**
 * The {@code TypeBasedSpecification} consists of an {@link Application},
 * {@link ResourceTypes}, {@link LinkTypes}, and {@link Mappings}.
 * 
 * @author Valentina Richthammer
 * 
 */
public class TypeBasedSpecification implements IAttributes, ApplicationProvider {

	protected Application<?, ?> application = null;
	protected Mappings<?, ?> mappings = null;
	protected ResourceTypes<?> resourceTypes = null;
	protected LinkTypes<?> linkTypes = null;
	protected Attributes attributes = new Attributes();

	/**
	 * Constructs the specification.
	 * 
	 * @param application
	 *            the application
	 * @param resourceTypes
	 *            the set of resource types
	 * @param mappings
	 *            the type mappings
	 */
	public TypeBasedSpecification(Application<?, ?> application, ResourceTypes<?> resourceTypes,
			Mappings<?, ?> mappings, LinkTypes<?> linkTypes) {
		this.resourceTypes = resourceTypes;
		this.application = application;
		this.mappings = mappings;
		this.linkTypes = linkTypes;
	}

	/**
	 * Returns the set of resource types.
	 * 
	 * @param <A>
	 *            the type of architecture
	 * @return the architecture
	 */
	@SuppressWarnings("unchecked")
	public <L extends LinkTypes<?>> L getLinkTypes() {
		return (L) linkTypes;
	}

	/**
	 * Returns the set of resource types.
	 * 
	 * @param <A>
	 *            the type of architecture
	 * @return the architecture
	 */
	@SuppressWarnings("unchecked")
	public <R extends ResourceTypes<Resource>> R getResourceTypes() {
		return (R) resourceTypes;
	}

	/**
	 * Returns the application.
	 * 
	 * @param <A>
	 *            the type of application
	 * @return the application
	 */
	@SuppressWarnings("unchecked")
	public <A extends Application<Task, Dependency>> A getApplication() {
		return (A) application;
	}

	/**
	 * Returns the mappings.
	 * 
	 * @param <M>
	 *            the type of mappings
	 * @return the mappings
	 */
	@SuppressWarnings("unchecked")
	public <M extends Mappings<Task, Resource>> M getMappings() {
		return (M) mappings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String identifier, Object object) {
		attributes.setAttribute(identifier, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttribute(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <O> O getAttribute(String identifier) {
		return (O) attributes.getAttribute(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.adse.model.IAttributes#getAttributeParameter(java.lang.String)
	 */
	@Override
	public Parameter getAttributeParameter(String identifier) {
		return attributes.getAttributeParameter(identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttributes()
	 */
	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttributeNames()
	 */
	@Override
	public Set<String> getAttributeNames() {
		return attributes.getAttributeNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#isDefined(java.lang.String)
	 */
	@Override
	public boolean isDefined(String identifier) {
		return attributes.isDefined(identifier);
	}

}
