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
package net.sf.opendse.realtime.et.test;

import net.sf.jmpi.main.MpSolver;
import net.sf.jmpi.solver.gurobi.SolverGurobi;
import net.sf.opendse.io.SpecificationReader;
import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.realtime.et.PriorityScheduler;
import net.sf.opendse.realtime.et.SolverProvider;
import net.sf.opendse.realtime.et.qcqp.MyConflictRefinement.ConflictRefinementMethod;
import net.sf.opendse.realtime.et.qcqp.MyEncoder.OptimizationObjective;
import net.sf.opendse.visualization.SpecificationViewer;

public class SchedulingStarter {

	//public static Set<TimingElement> iis = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SolverProvider solverProvider = new SolverProvider() {
			@Override
			public MpSolver get() {
				MpSolver solver = new SolverGurobi();
				solver.setTimeout(3600);
				return solver;
			}
		};

		String method = args[0];
		String inputFile = args[1];
		String outputFile = args[2];

		SpecificationWriter writer = new SpecificationWriter();
		SpecificationReader reader = new SpecificationReader();

		Specification implementation = reader.read(inputFile);

		for(Task task: implementation.getApplication()){
			task.setAttribute("prio", null);
		}
		startTimer();

		// "-qcqp", "-qcqpD", "-qcqpE"
		if (method.startsWith("-qcqp")) {
			PriorityScheduler scheduler = new PriorityScheduler(implementation, solverProvider, false);

			if (!scheduler.solve(OptimizationObjective.DELAY)) {
				System.out.println("Problem is infeasible.");
				if (!method.equalsIgnoreCase("-qcqp")) {

					if (method.endsWith("D")) {
						scheduler.determineIIS(ConflictRefinementMethod.DELETION);
					} else if (method.endsWith("H")) {
						scheduler.determineIIS(ConflictRefinementMethod.HIERARCHICAL);
					} else {
						throw new IllegalArgumentException("unknown refinement method " + method);
					}
				}
			}
		}

		System.out.println("runtime " + getTime());

		implementation.setAttribute("runtime", getTime());

		writer.write(implementation, outputFile);

		SpecificationViewer.view(implementation);

	}

	protected static long start = 0;

	protected static void startTimer() {
		start = System.currentTimeMillis();
	}

	protected static double getTime() {
		return ((double) System.currentTimeMillis() - start) / 1000.0;
	}

}
