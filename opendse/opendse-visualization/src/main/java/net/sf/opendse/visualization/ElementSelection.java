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
