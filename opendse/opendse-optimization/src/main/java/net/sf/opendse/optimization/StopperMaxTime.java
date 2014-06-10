package net.sf.opendse.optimization;

import org.opt4j.core.Individual;
import org.opt4j.core.IndividualStateListener;
import org.opt4j.core.optimizer.Control;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

public class StopperMaxTime implements IndividualStateListener {

	protected final Control control;
	protected final int maxTime;
	protected final long startTime;

	@Inject
	public StopperMaxTime(Control control, @Constant(namespace = StopperMaxTime.class, value = "maxTime") int maxTime) {
		super();
		this.control = control;
		this.maxTime = maxTime;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void inidividualStateChanged(Individual individual) {
		long diff = (System.currentTimeMillis() - startTime) / 1000;

		if (diff >= maxTime) {
			control.doStop();
		}
	}

}
