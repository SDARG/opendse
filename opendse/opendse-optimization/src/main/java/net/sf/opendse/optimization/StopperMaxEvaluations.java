package net.sf.opendse.optimization;

import org.opt4j.core.Individual;
import org.opt4j.core.IndividualStateListener;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

public class StopperMaxEvaluations implements IndividualStateListener {

	protected final int maxEvaluations;
	protected int evaluations = 0;
	protected final Control control;

	@Inject
	public StopperMaxEvaluations(Control control, @Constant(namespace=StopperMaxEvaluations.class, value="maxEvaluations") int maxEvaluations) {
		super();
		this.maxEvaluations = maxEvaluations;
		this.control = control;
	}

	@Override
	public synchronized void inidividualStateChanged(Individual individual) {
		if (individual.getState().isEvaluated()) {
			evaluations++;
			if(evaluations >= maxEvaluations){
				control.doStop();
			}
		}
	}

}
