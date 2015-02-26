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

import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.parameter.Parameter;

/**
 * The {@code Element} is the default element.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Element implements IAttributes {

	/**
	 * The identifier for the type of an element.
	 */
	public static String TYPE = "TYPE";

	/**
	 * The local attributes of this element.
	 */
	protected final Attributes attributes = new Attributes();

	/**
	 * The parent element.
	 */
	protected Element parent = null;

	/**
	 * The unique id.
	 */
	protected String id = null;

	/**
	 * Constructs a new {@code Element}.
	 * 
	 * @param id
	 *            the {@code id}
	 */
	public Element(String id) {
		super();
		this.id = id;
	}

	/**
	 * Constructs a new {@code Element} from a parent, i.e., a copy with local
	 * attributes.
	 * 
	 * @param parent
	 *            the parent
	 */
	public Element(Element parent) {
		this(parent.getId());
		this.parent = parent;
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the parent element.
	 * 
	 * @return the parent element
	 */
	public Element getParent() {
		return parent;
	}

	/**
	 * Sets the parent element. This is only feasible, if the parent is not set
	 * yet. Changing a parent is not permitted.
	 * 
	 * @param element
	 *            the parent
	 */
	public void setParent(Element element) {
		if (parent != null) {
			throw new IllegalStateException("Parent element has already been defined.");
		}
		this.parent = element;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttribute(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <O> O getAttribute(String identifier) {
		if (attributes.getAttributes().containsKey(identifier)) {
			return (O) attributes.getAttribute(identifier);
		} else if (parent != null) {
			return (O) parent.getAttribute(identifier);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#isDefined(java.lang.String)
	 */
	@Override
	public boolean isDefined(String identifier) {
		if (attributes.isDefined(identifier)) {
			return true;
		} else if (parent != null) {
			return parent.isDefined(identifier);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttributes()
	 */
	@Override
	public Attributes getAttributes() {
		Attributes map = new Attributes();
		if (parent != null) {
			map.putAll(parent.getAttributes());
		}
		map.putAll(attributes.getAttributes());
		return map;
	}

	/**
	 * Returns the local attributes.
	 * 
	 * @return the local attributes
	 */
	public Attributes getLocalAttributes() {
		return attributes;
	}

	/**
	 * Returns the local attribute names.
	 * 
	 * @return the local attribute names
	 */
	public Set<String> getLocalAttributeNames() {
		return attributes.getAttributeNames();
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Element other = (Element) obj;
		return this.id.equals(other.id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.adse.model.IAttributes#getAttributeParameter(java.lang.String)
	 */
	@Override
	public Parameter getAttributeParameter(String identifier) {
		Parameter parameter = attributes.getAttributeParameter(identifier);
		Object attribute = attributes.getAttribute(identifier);
		if (parameter != null) {
			return parameter;
		} else if (parameter == null && attribute != null) {
			return null;
		} else if (getParent() != null) {
			return getParent().getAttributeParameter(identifier);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.adse.model.IAttributes#getAttributeNames()
	 */
	@Override
	public Set<String> getAttributeNames() {
		Set<String> names = new HashSet<String>();
		if (parent != null) {
			names.addAll(parent.getAttributeNames());
		}
		names.addAll(attributes.getAttributeNames());
		return names;
	}

	/**
	 * Returns the type of the element.
	 * 
	 * @return the type of the element
	 */
	public String getType() {
		return getAttribute(TYPE);
	}

	/**
	 * Sets the type of the element.
	 * 
	 * @param type
	 *            the type of the element
	 */
	public void setType(String type) {
		setAttribute(TYPE, type);
	}

}
