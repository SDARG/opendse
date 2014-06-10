package net.sf.opendse.optimization;

import org.opt4j.core.Individual;
import org.opt4j.core.IndividualSet;
import org.opt4j.core.IndividualSetListener;
import org.opt4j.core.common.archive.CrowdingArchive;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.OptimizerIterationListener;
import org.opt4j.core.optimizer.Population;

import com.google.inject.Inject;

public class StagnationRestart implements IndividualSetListener, OptimizerIterationListener {

	protected final Archive archive = new CrowdingArchive(100);
	protected final Population population;

	protected int iteration = 0;
	protected int lastUpdate = 0;
	protected final int diff = 20;

	@Inject
	public StagnationRestart(Population population) {
		this.population = population;
	}

	@Override
	public void iterationComplete(int iteration) {
		this.iteration = iteration;

		for (Individual in0 : population) {
			for (Individual in1 : archive) {
				if (in0.getObjectives().dominates(in1.getObjectives())) {
					lastUpdate = iteration;
				}
			}
		}

		archive.update(population);

		// System.out.println(iteration-lastUpdate);

		if (iteration - lastUpdate > diff) {
			lastUpdate = iteration;
			archive.clear();
			population.clear();
		}

	}


	@Override
	public void individualAdded(IndividualSet collection, Individual individual) {
		// TODO Auto-generated method stub
	}

	@Override
	public void individualRemoved(IndividualSet collection, Individual individual) {
		// TODO Auto-generated method stub	
	}

}
