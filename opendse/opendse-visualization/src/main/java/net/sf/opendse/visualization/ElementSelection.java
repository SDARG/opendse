/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
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
