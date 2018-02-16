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
