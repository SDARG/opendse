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

/**
 * The {@code Task} is the basic vertex element for {@link Application} graphs.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class TgffTask extends Task {
	
	// period (name required by opendse-realtime...)
	public static String PERIOD = "h";
	public static String WCET = "e";
	public static String TGFF_TYPE = "TGFF_TYPE";

	/**
	 * Constructs a new task.
	 * 
	 * @param id
	 *            the id
	 */
	public TgffTask(String id, String tgffType, double period) {
		super(id);
		
		//this.setType(type);
		this.setTgffType(tgffType);
		this.setPeriod(period);
	}

	/**
	 * Constructs a new parent.
	 * 
	 * @param parent
	 *            the parent
	 */
	public TgffTask(Element parent) {
		super(parent);
	}
	
	
	/**
	 * Returns the period of the element.
	 * 
	 * @return the period of the element
	 */
	public double getPeriod() {
		return getAttribute(PERIOD);
	}

	/**
	 * Sets the period of the element.
	 * 
	 * @param type
	 *            the period of the element
	 */
	public void setPeriod (double period) {
		setAttribute(PERIOD, period);
	}
	
	/**
	 * Returns the period of the element.
	 * 
	 * @return the period of the element
	 */
	public double getWCET() {
		return getAttribute(WCET);
	}

	/**
	 * Sets the period of the element.
	 * 
	 * @param type
	 *            the period of the element
	 */
	public void setWCET (double wcet) {
		setAttribute(WCET, wcet);
	}
	
	/**
	 * Returns the type of the element.
	 * 
	 * @return the type of the element
	 */
	public String getTgffType() {
		return getAttribute(TGFF_TYPE);
	}

	/**
	 * Sets the type of the element.
	 * 
	 * @param type
	 *            the type of the element
	 */
	public void setTgffType(String tgffType) {
		setAttribute(TGFF_TYPE, tgffType);
	}

}
