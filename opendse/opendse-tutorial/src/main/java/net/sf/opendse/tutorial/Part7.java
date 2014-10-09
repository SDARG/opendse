package net.sf.opendse.tutorial;

import net.sf.opendse.io.SpecificationReader;
import net.sf.opendse.model.Specification;
import net.sf.opendse.realtime.et.PriorityScheduler;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.OptimizationObjective;
import net.sf.opendse.visualization.SpecificationViewer;

public class Part7 {

	public static void main(String[] args) throws Exception {
		/*
		 * The implementation contains information about the execution time of
		 * tasks (e) and their period (h). The periods of a function have to be
		 * all the same. Additionally, tasks have deadlines (deadlines).
		 * Finally, based on the type of scheduler (FIXEDPRIORITY_NONPREEMPTIVE
		 * or FIXEDPRIORITY_PREEMPTIVE) on the resources, the scheduling is
		 * performed. Note that the deadline has to be less or equal to the
		 * period of a task.
		 */
		SpecificationReader reader = new SpecificationReader();
		Specification implementation = reader.read("specs/Implementation7.xml");

		/*
		 * The scheduling determines priorities for all tasks. For messages,
		 * multiple priorities are assigned per resource it passes.
		 */
		PriorityScheduler scheduler = new PriorityScheduler(implementation);
		scheduler.solve(OptimizationObjective.DELAY);

		SpecificationViewer.view(implementation);
	}

}
