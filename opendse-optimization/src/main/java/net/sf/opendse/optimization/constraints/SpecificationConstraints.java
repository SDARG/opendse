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
	
	public static String ELEMENTS_EXCLUDE = "ELEMENTS_EXCLUDE";
	public static String ELEMENTS_REQUIRE = "ELEMENTS_REQUIRE";
	
	public static String ROUTER = "ROUTER";

}
