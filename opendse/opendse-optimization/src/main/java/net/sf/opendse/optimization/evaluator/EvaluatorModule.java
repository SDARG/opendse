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
