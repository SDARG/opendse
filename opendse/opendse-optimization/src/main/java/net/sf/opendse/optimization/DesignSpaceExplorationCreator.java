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

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.problem.Creator;

import com.google.inject.Inject;

public class DesignSpaceExplorationCreator implements Creator<CompositeGenotype<String, Genotype>> {

	protected final SATCreatorDecoder satCreator;
	protected final ParameterCreator parameterCreator;

	@Inject
	public DesignSpaceExplorationCreator(SATCreatorDecoder satCreator, ParameterCreator parameterCreator) {
		super();
		this.satCreator = satCreator;
		this.parameterCreator = parameterCreator;
	}

	@Override
	public CompositeGenotype<String, Genotype> create() {
		CompositeGenotype<String, Genotype> g = new CompositeGenotype<String, Genotype>();
		g.put("SAT", satCreator.create());
		g.put("PARAMETER", parameterCreator.create());
		return g;
	}

}
