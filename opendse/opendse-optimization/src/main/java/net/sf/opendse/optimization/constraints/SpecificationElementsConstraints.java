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
				System.out.println(c);
				constraints.add(c);
			}
			if(requireList != null){
				Constraint c = new Constraint(">=", 0);
				c.add(-requireList.size(), p(element));
				for(String str: requireList){
					c.add(p(elementsMap.get(str)));
				}
				System.out.println(c);
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
