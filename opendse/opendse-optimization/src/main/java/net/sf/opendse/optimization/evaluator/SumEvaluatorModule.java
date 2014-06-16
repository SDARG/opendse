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

import net.sf.opendse.optimization.ImplementationEvaluator;

import org.opt4j.core.config.annotations.Multi;
import org.opt4j.core.config.annotations.Order;

import com.google.inject.multibindings.Multibinder;

@Multi
public class SumEvaluatorModule extends EvaluatorModule {

	@Order(0)
	protected String sum = "area,power";
	@Order(2)
	protected int priority = 0;
	@Order(1)
	protected Type type = Type.MIN;
	
	public enum Type {
		MIN,MAX;
	}
	
	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	protected void config() {
		SumEvaluator evaluator = new SumEvaluator(sum, priority, type == Type.MIN);
		
		Multibinder<ImplementationEvaluator> multibinder = Multibinder.newSetBinder(binder(),
				ImplementationEvaluator.class);
		multibinder.addBinding().toInstance(evaluator);
	}

}
