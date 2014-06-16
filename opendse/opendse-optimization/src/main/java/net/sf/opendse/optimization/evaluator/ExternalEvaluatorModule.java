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

import com.google.inject.multibindings.Multibinder;

@Multi
public class ExternalEvaluatorModule extends EvaluatorModule {

	protected String command = "java -jar C:/Users/lukasiew/eclipse/workspace1/CustomEvaluator/build/jar/myeval-0.1.jar area power";
	protected int priority = 0;
	protected Type type = Type.FILE;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public enum Type {
		FILE, STREAM;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	protected void config() {
		ImplementationEvaluator evaluator = null;
		switch (type) {
		case STREAM:
			evaluator = new ExternalEvaluatorStream(command, priority);
			break;
		default: // FILE
			evaluator = new ExternalEvaluatorFile(command, priority);
			break;
		}

		Multibinder<ImplementationEvaluator> multibinder = Multibinder.newSetBinder(binder(),
				ImplementationEvaluator.class);
		multibinder.addBinding().toInstance(evaluator);
	}
}
