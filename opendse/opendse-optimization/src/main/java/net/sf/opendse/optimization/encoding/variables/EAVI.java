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
package net.sf.opendse.optimization.encoding.variables;

import net.sf.opendse.model.Element;

public class EAVI extends Variable {

	public EAVI(Element e, String attribute, Object value, Integer index){
		super(e,attribute,value,index);
	}
	
	public Element getE(){
		return get(0);
	}
	
	public String getA(){
		return get(1);
	}
	
	public Object getV(){
		return get(2);
	}
	
	public Integer getI(){
		return get(3);
	}
	
	
	
}
