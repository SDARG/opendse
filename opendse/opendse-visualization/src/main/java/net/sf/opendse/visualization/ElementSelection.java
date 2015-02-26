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
package net.sf.opendse.visualization;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.sf.opendse.model.Element;

public class ElementSelection {

	protected Set<ElementSelectionListener> listeners = new CopyOnWriteArraySet<ElementSelectionListener>();
	protected Element selected = null;

	@SuppressWarnings("unchecked")
	public <E extends Element> E get() {
		return (E)selected;
	}
	
	public boolean isSelected(Element element){
		return element.equals(selected);
	}
	
	public boolean isNull(){
		return selected == null;
	}

	public void set(Element element) {
		selected = element;
		callListeners();
	}
	
	protected void callListeners(){
		for(ElementSelectionListener listener: listeners){
			listener.selectionChanged(this);
		}
	}
	
	public void addListener(ElementSelectionListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(ElementSelectionListener listener){
		listeners.remove(listener);
	}

}
