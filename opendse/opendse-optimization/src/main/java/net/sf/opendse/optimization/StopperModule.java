/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
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
