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

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Solver;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SATIncremental {

	protected final SATConstraints satConstraints;
	protected final Solver solver;

	@Inject
	public SATIncremental(SATConstraints satConstraints, Solver solver) {
		super();
		this.satConstraints = satConstraints;
		this.solver = solver;
	}

	public void exclude(Constraint constraint) {
		Constraint c = satConstraints.getPreprocessing().processAfterInit(constraint);
		solver.addConstraint(c);
	}

}
