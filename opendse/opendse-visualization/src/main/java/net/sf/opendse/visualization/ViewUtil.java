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

import java.lang.reflect.Array;

import net.sf.opendse.model.Element;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.parameter.Parameter;

public class ViewUtil {

	public static String getTooltip(Element element) {
		if(element == null){
			return "";
		}
		return "<html>" + getTooltipRecursive(element) + "</html>";
	}
	
	public static String getTooltip(Specification specification){
		
		int resources = specification.getArchitecture().getVertexCount();
		int links = specification.getArchitecture().getEdgeCount();
		int mappings = specification.getMappings().size();
		int processes = 0;
		int communications = 0;
		for(Task task: specification.getApplication()){
			if(Models.isProcess(task)){
				processes++;
			} else {
				communications++;
			}
		}
		int dependencies = specification.getApplication().getEdgeCount();
		
		String s = "<html>";
		s += "specification <br>";
		s += "architecture (#resources="+resources+", #links="+links+") <br>";
		s += "application (#processes="+processes+", #communications="+communications+", #dependencies="+dependencies+")  <br>";
		s += "mappings (#mappings="+mappings+")<br>";
		s += "attributes:<br>";
		for (String attributeName : specification.getAttributeNames()) {
			String name = attributeName;
			Object value = specification.getAttribute(attributeName);
			s += " - " + name + " = " + objectToString(value) +"<br>";			
		}
		s += "</html>";
		return s;
	}

	private static String getTooltipRecursive(Element element) {
		String s = "";
		if (element.getParent() != null) {
			s += getTooltipRecursive(element.getParent());
		}
		s += "ID=" + element.getId() + " ("+element.getClass().getSimpleName()+")<br>";

		for (String attributeName : element.getLocalAttributeNames()) {
			Parameter parameter = element.getAttributeParameter(attributeName);
			if(parameter != null){
				s += " - " + attributeName + " = " + parameter +"<br>";
			} else {
				Object object = element.getAttribute(attributeName);
				s += " - " + attributeName + " = " + objectToString(object) +"<br>";
			}
			
			
		}
		return s;
	}
	
	protected static String objectToString(Object object) {
		if (object == null) {
			return "null";
		} else if (object.getClass().isArray()) {
			int length = Array.getLength(object);
			
			String s = "[";
			
			for(int i=0; i<length; i++){
				Object obj = Array.get(object, i);
				s += objectToString(objectToString(obj));
				if(i < length - 1){
					s += " ";
				}
			}
			s += "]";
			return s;	
		} else {
			return object.toString();
		}
	}

}
