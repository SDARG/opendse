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
package net.sf.opendse.optimization.constraints;

import java.util.Collection;
import java.util.Set;

import net.sf.opendse.model.Specification;
import net.sf.opendse.model.parameter.ParameterReference;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;

public interface SpecificationConstraints {
	
	public void doEncoding(Collection<Constraint> constraints);
	
	public void doInterpreting(Specification implementation, Model model);
	
	public Set<ParameterReference> getActiveParameters();

	public static String CONNECT = ":CONNECT";
	public static String CONNECT_MAX = ":CONNECT-MAX";
	public static String CONNECT_MIN = ":CONNECT-MIN";
	
	public static String CAPACITY = ":CAPACITY";
	public static String CAPACITY_MAX = ":CAPACITY-MAX";
	public static String CAPACITY_MIN = ":CAPACITY-MIN";
	public static String CAPACITY_SCALE = ":CAPACITY-SCALE";
	public static String CAPACITY_ACTION = ":CAPACITY-ACTION";
	public static String CAPACITY_VALUE = ":CAPACITY-VALUE";
	public static String CAPACITY_RATIO = ":CAPACITY-RATIO";
	
	public static String ROUTER = "ROUTER";

}
