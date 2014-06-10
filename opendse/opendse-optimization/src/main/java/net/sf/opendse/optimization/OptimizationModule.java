package net.sf.opendse.optimization;

import net.sf.opendse.model.constraints.SpecificationCapacityConstraints;
import net.sf.opendse.model.constraints.SpecificationConnectConstraints;
import net.sf.opendse.model.constraints.SpecificationConstraints;
import net.sf.opendse.model.constraints.SpecificationConstraintsMulti;
import net.sf.opendse.model.constraints.SpecificationRouterConstraints;

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
