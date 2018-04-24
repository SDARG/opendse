package net.sf.opendse.tutorial;

import net.sf.opendse.io.SpecificationReader;
import net.sf.opendse.io.TgffImport;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.PriorityScheduler;
import net.sf.opendse.realtime.et.TimingGraphViewer;
import net.sf.opendse.realtime.et.graph.TimingGraph;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.OptimizationObjective;
import net.sf.opendse.visualization.SpecificationViewer;

public class TgffTest {

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
		
		TimingGraph reduced = scheduler.getReducedTimingGraph();
		
		//TimingGraphViewer.view(reduced);
		
		
		TgffImport importer = new TgffImport("specs/auto-indust-mocsyn.tgff");
		Application<Task, Dependency> app = importer.getApplication();
		
		Architecture arch = importer.getArchitecture(8, 8, true);
		
		Specification spec = new Specification(app, implementation.getArchitecture(), implementation.getMappings());
		
		SpecificationViewer.view(spec);
		

		//SpecificationViewer.view(implementation);
	}

}
