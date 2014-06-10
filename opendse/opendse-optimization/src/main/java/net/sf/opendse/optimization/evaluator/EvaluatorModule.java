package net.sf.opendse.optimization.evaluator;

import net.sf.opendse.optimization.DesignSpaceExplorationModule;
import net.sf.opendse.optimization.ImplementationEvaluator;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Category;
import org.opt4j.core.config.annotations.Icon;

import com.google.inject.multibindings.Multibinder;

@Category
@Icon(Icons.PUZZLE_BLUE)
public abstract class EvaluatorModule extends DesignSpaceExplorationModule {

	protected void bindEvaluator(Class<? extends ImplementationEvaluator> clazz) {
		Multibinder<ImplementationEvaluator> multibinder = Multibinder.newSetBinder(binder(),
				ImplementationEvaluator.class);
		multibinder.addBinding().to(clazz);
	}
}
