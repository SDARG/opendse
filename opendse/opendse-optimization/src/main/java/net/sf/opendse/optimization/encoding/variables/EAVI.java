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
