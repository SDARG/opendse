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
