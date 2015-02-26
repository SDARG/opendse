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
package net.sf.opendse.optimization.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.opendse.model.Element;

public class ElementList extends HashSet<String> {
	private static final long serialVersionUID = 1L;

	public ElementList() {
		super();
	}

	public ElementList(Collection<? extends String> c) {
		super(c);
	}
	
	public ElementList(String... elements){
		for(String value: elements){
			add(value.trim());
		}
	}
	
	public ElementList(Element... elements){
		for(Element element: elements){
			add(element.getId().trim());
		}
	}

	public ElementList with(String element){
		ElementList result = new ElementList(this);
		result.add(element);
		return result;
	}
	
	public ElementList without(String element){
		ElementList result = new ElementList(this);
		result.remove(element);
		return result;
	}
	
	public ElementList with(Element element){
		return with(element.getId());
	}
	
	public ElementList without(Element element){
		return without(element.getId());
	}
	
	
	public static ElementList elements(Collection<? extends Element> c){
		List<String> elementIds = new ArrayList<String>();
		for(Element element: c){
			elementIds.add(element.getId());
		}
		return new ElementList(elementIds);
	}
	
	public static ElementList elements(String... elements){
		return new ElementList(elements);
	}
	
	public static ElementList elements(Element... elements){
		return new ElementList(elements);
	}

	public static ElementList parseElements(String elements){
		String[] parts = elements.split("\\|");
		return elements(parts);
	}
	
	public String toString(){
		int i = 0;
		String s = "";
		for(String element: this){
			s += element;
			if(++i < this.size()){
				s += "|";
			}
		}
		return s;
	}
	
}
