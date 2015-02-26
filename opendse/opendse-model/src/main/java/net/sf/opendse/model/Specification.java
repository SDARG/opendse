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
package net.sf.opendse.model;

import java.util.Set;

import net.sf.opendse.model.parameter.Parameter;

/**
 * The {@code Specification} consists of an {@link Application},
 * {@link Architecture}, {@link Mappings}, and {@link Routings}.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Specification implements IAttributes {

	protected Architecture<?, ?> architecture = null;
	protected Application<?, ?> application = null;
	protected Mappings<?, ?> mappings = null;
	protected Routings<?, ?, ?> routings = null;
	protected Attributes attributes = new Attributes();

	/**
	 * Constructs the specification. The routings are the architecture for each
	 * task.
	 * 
	 * @param application
	 *            the application
	 * @param architecture
	 *            the architecture
	 * @param mappings
	 *            the mappings
	 */
	public Specification(Application<?, ?> application, Architecture<?, ?> architecture, Mappings<?, ?> mappings) {
		this(application, architecture, mappings, fill(application, architecture));
	}

	protected static Routings<?, ?, ?> fill(Application<?, ?> application, Architecture<?, ?> architecture) {
		Routings<Task, Resource, Link> routings = new Routings<Task, Resource, Link>();
		@SuppressWarnings("unchecked")
		Architecture<Resource, Link> arch = (Architecture<Resource, Link>)architecture;

		for (Task task : application) {
			if (task instanceof ICommunication) {
				Architecture<Resource, Link> routing = new Architecture<Resource, Link>();

				for (Resource resource : architecture.getVertices()) {
					routing.addVertex(resource);
				}
				for (Link link : architecture.getEdges()) {
					routing.addEdge(link, arch.getEndpoints(link), arch.getEdgeType(link));
				}
				routings.set(task, routing);
			}
		}

		return routings;
	}

	/**
	 * Constructs the specification.
	 * 
	 * @param application
	 *            the application
	 * @param architecture
	 *            the architecture
	 * @param mappings
	 *            the mappings
	 * @param routings
	 *            the routings
	 */
	public Specification(Application<?, ?> application, Architecture<?, ?> architecture, Mappings<?, ?> mappings,
			Routings<?, ?, ?> routings) {
		super();
		this.architecture = architecture;
		this.application = application;
		this.mappings = mappings;
		this.routings = routings;
	}

	/**
	 * Returns the architecture.
	 * 
	 * @param <A>
	 *            the type of architecture
	 * @return the architecture
	 */
	@SuppressWarnings("unchecked")
	public <A extends Architecture<Resource, Link>> A getArchitecture() {
		return (A) architecture;
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

	/**
	 * Returns the routings.
	 * 
	 * @param <R>
	 *            the type of routings
	 * @return the routings
	 */
	@SuppressWarnings("unchecked")
	public <R extends Routings<Task, Resource, Link>> R getRoutings() {
		return (R) routings;
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
