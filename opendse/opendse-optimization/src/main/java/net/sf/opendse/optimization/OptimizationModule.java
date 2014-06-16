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

import net.sf.opendse.optimization.constraints.SpecificationCapacityConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConnectConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConstraintsMulti;
import net.sf.opendse.optimization.constraints.SpecificationRouterConstraints;

import org.opt4j.core.config.annotations.Parent;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.viewer.VisualizationModule;

import com.google.inject.multibindings.Multibinder;

@Parent(DesignSpaceExplorationModule.class)
public class OptimizationModule extends ProblemModule {

	@Override
	protected void config() {
		bindProblem(DesignSpaceExplorationCreator.class, DesignSpaceExplorationDecoder.class,
				DesignSpaceExplorationEvaluator.class);

		VisualizationModule.addIndividualMouseListener(binder(), ImplementationWidgetService.class);
		VisualizationModule.addToolBarService(binder(), SpecificationToolBarService.class);

		bind(SpecificationConstraints.class).to(SpecificationConstraintsMulti.class).in(SINGLETON);
		Multibinder<SpecificationConstraints> scmulti = Multibinder.newSetBinder(binder(),
				SpecificationConstraints.class);
		scmulti.addBinding().to(SpecificationCapacityConstraints.class);
		scmulti.addBinding().to(SpecificationConnectConstraints.class);
		scmulti.addBinding().to(SpecificationRouterConstraints.class);

		Multibinder.newSetBinder(binder(), ImplementationEvaluator.class);

		addOptimizerIterationListener(StagnationRestart.class);

		//bind(SATManager.class).to(MyMixedSATManager.class);

	}

}
