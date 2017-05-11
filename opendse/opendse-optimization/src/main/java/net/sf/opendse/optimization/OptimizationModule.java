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

import net.sf.opendse.optimization.constraints.SpecificationCapacityConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConnectConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConstraints;
import net.sf.opendse.optimization.constraints.SpecificationConstraintsMulti;
import net.sf.opendse.optimization.constraints.SpecificationElementsConstraints;
import net.sf.opendse.optimization.constraints.SpecificationRouterConstraints;
import net.sf.opendse.optimization.encoding.Encoding.RoutingEncoding;

import org.opt4j.core.config.annotations.Parent;
import org.opt4j.core.problem.ProblemModule;
import org.opt4j.core.start.Constant;
import org.opt4j.viewer.VisualizationModule;

import com.google.inject.multibindings.Multibinder;

@Parent(DesignSpaceExplorationModule.class)
public class OptimizationModule extends ProblemModule {

	protected RoutingEncoding routingEncoding = RoutingEncoding.FLOW;
	
	@Constant(value = "preprocessing", namespace = SATConstraints.class)
	protected boolean usePreprocessing = true;
	
	
	@Constant(value = "variableorder", namespace = SATCreatorDecoder.class)
	protected boolean useVariableOrder = true;
	
	public RoutingEncoding getRoutingEncoding() {
		return routingEncoding;
	}

	public void setRoutingEncoding(RoutingEncoding routingEncoding) {
		this.routingEncoding = routingEncoding;
	}

	public boolean isUsePreprocessing() {
		return usePreprocessing;
	}

	public void setUsePreprocessing(boolean usePreprocessing) {
		this.usePreprocessing = usePreprocessing;
	}

	public boolean isUseVariableOrder() {
		return useVariableOrder;
	}

	public void setUseVariableOrder(boolean useVariableOrder) {
		this.useVariableOrder = useVariableOrder;
	}

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
		scmulti.addBinding().to(SpecificationElementsConstraints.class);
		scmulti.addBinding().to(SpecificationRouterConstraints.class);

		Multibinder.newSetBinder(binder(), ImplementationEvaluator.class);

		addOptimizerIterationListener(StagnationRestart.class);
		
		bind(RoutingEncoding.class).toInstance(routingEncoding);

		if (useVariableOrder){
			bind(VariableClassOrder.class).to(RoutingVariableClassOrder.class);
		}

	}

}
