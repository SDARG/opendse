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

import java.util.Arrays;

public abstract class Variable {

	protected Object[] objects;
	                 
	public Variable(Object... objects){
		super();
		final int n = objects.length;
		this.objects = new Object[n+1];
		this.objects[0] = this.getClass();
		System.arraycopy(objects, 0, this.objects, 1, n);
	}
	
	@SuppressWarnings("unchecked")
	public <O> O get(int i){
		return (O)objects[i+1];
	}
	                 
	           
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(objects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Variable other = (Variable) obj;
		return Arrays.equals(this.objects, other.objects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		s += this.getClass().getSimpleName();
		s += "[";
		for(int i=1; i<objects.length; i++){
			s += objects[i] + ",";
		}
		s = s.substring(0, s.length() - 1);
		s += "]";
		return s;
	}


}
