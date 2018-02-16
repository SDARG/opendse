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
