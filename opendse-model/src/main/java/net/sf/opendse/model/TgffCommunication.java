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
 * The {@code Communication} is the default implementation of the
 * {@link ICommunication} interface.
 * 
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class TgffCommunication extends Communication implements ICommunication {
	
	private final static String MESSAGE_SIZE = "MESSAGE_SIZE";
	private final static String PERIOD = "PERIOD";

	/**
	 * Constructs a new communication.
	 * 
	 * @param id
	 *            the id
	 */
	public TgffCommunication(String id, double msgSize, double period) {
		super(id);
		
		this.setMsgSize(msgSize);
		this.setPeriod(period);
	}

	/**
	 * Constructs a new communication.
	 * 
	 * @param parent
	 *            the parent
	 */
	public TgffCommunication(Element parent) {
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
	 * Returns the type of the element.
	 * 
	 * @return the type of the element
	 */
	public String getMsgSize() {
		return getAttribute(MESSAGE_SIZE);
	}

	/**
	 * Sets the type of the element.
	 * 
	 * @param type
	 *            the type of the element
	 */
	public void setMsgSize(double size) {
		setAttribute(MESSAGE_SIZE, size);
	}

}
