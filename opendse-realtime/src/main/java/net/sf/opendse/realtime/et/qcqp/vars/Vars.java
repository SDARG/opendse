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
package net.sf.opendse.realtime.et.qcqp.vars;

import static net.sf.jmpi.main.expression.MpExpr.var;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.graph.TimingDependency;
import net.sf.opendse.realtime.et.graph.TimingDependencyPriority;
import net.sf.opendse.realtime.et.graph.TimingElement;

public class Vars {

	public static Object r(TimingElement t) {
		return var("r", t);
	}

	public static Object jIn(TimingElement t) {
		return var("j^in", t);
	}
	
	public static Object jOut(TimingElement t) {
		return var("j^out", t);
	}

	public static Object i(TimingElement t1, TimingElement t2) {
		return var("i", t1, t2);
	}

	public static Object d(TimingElement t) {
		return var("d", t);
	}

	public static Object b(TimingElement t) {
		return var("b", t);
	}

	public static Object c(TimingElement t) {
		return var("c", t);
	}

	public static Object a(TimingDependencyPriority tdp) {
		return var("a", tdp);
	}

	public static Object p(Task t1, Task t2, Resource r) {
		return var("p", t1, t2, r);
	}

	public static Object a(TimingDependency tdp) {
		return a((TimingDependencyPriority) tdp);
	}

}
