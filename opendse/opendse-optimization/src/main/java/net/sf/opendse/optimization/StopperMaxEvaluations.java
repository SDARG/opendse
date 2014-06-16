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
