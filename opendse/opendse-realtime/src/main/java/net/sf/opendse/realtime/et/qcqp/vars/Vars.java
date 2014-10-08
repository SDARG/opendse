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
