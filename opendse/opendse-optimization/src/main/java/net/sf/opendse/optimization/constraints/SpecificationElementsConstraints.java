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

import static net.sf.opendse.optimization.encoding.variables.Variables.p;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.parameter.ParameterReference;
import net.sf.opendse.optimization.SpecificationWrapper;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;

public class SpecificationElementsConstraints implements SpecificationConstraints {

	protected Specification specification;
	
	@Inject
	public SpecificationElementsConstraints(SpecificationWrapper specificationWrapper){
		this.specification = specificationWrapper.getSpecification();
	}
	
	@Override
	public void doInterpreting(Specification implementation, Model model) {
		
	}

	@Override
	public void doEncoding(Collection<Constraint> constraints) {
		
		Set<Element> elements = new HashSet<Element>();
		elements.addAll(specification.getArchitecture().getVertices());
		elements.addAll(specification.getArchitecture().getEdges());
		
		Map<String,Element> elementsMap = new HashMap<String,Element>();
		for(Element element: elements){
			elementsMap.put(element.getId(), element);
		}
		
		for(Element element: elements){
			ElementList excludeList = getElementList(element, ELEMENTS_EXCLUDE);
			ElementList requireList = getElementList(element, ELEMENTS_REQUIRE);
			
			if(excludeList != null){
				Constraint c = new Constraint("<=", excludeList.size());
				c.add(excludeList.size(), p(element));
				for(String str: excludeList){
					c.add(p(elementsMap.get(str)));
				}
				//System.out.println(c);
				constraints.add(c);
			}
			if(requireList != null){
				Constraint c = new Constraint(">=", 0);
				c.add(-requireList.size(), p(element));
				for(String str: requireList){
					c.add(p(elementsMap.get(str)));
				}
				//System.out.println(c);
				constraints.add(c);
			}
		}
		

	}
	
	protected ElementList getElementList(Element element, String attribute){
		if(element.getAttribute(attribute)!=null){
			Object object = element.getAttribute(attribute);
			ElementList list = null;
			if(object instanceof ElementList){
				list = (ElementList)object;
			} else if(object instanceof String){
				list = ElementList.parseElements((String)object);
			} else {
				throw new RuntimeException("Cannot parse attribute "+object+" of "+element);
			}
			return list;
		} else {
			return null;
		}
	}

	@Override
	public Set<ParameterReference> getActiveParameters() {
		return new HashSet<ParameterReference>();
	}

}
