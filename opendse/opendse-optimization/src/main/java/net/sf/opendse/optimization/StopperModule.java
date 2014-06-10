package net.sf.opendse.optimization;

import org.opt4j.core.start.Constant;

public class StopperModule extends DesignSpaceExplorationModule {

	@Constant(namespace=StopperMaxTime.class, value="maxTime")
	protected int maxTime = Integer.MAX_VALUE;
	 
	@Constant(namespace=StopperMaxEvaluations.class, value="maxEvaluations")
	protected int maxEvaluations = Integer.MAX_VALUE;
	
	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public void setMaxEvaluations(int maxEvaluations) {
		this.maxEvaluations = maxEvaluations;
	}

	@Override
	protected void config() {
		addIndividualStateListener(StopperMaxTime.class);
		addIndividualStateListener(StopperMaxEvaluations.class);

	}

}
