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

@Multi
public class ClassEvaluatorModule extends EvaluatorModule {

	protected String classname = "";
	protected String config = "";

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Override
	protected void config() {

		try {
			Class<? extends ImplementationEvaluator> clazz = Class.forName(classname).asSubclass(
					ImplementationEvaluator.class);
			bindEvaluator(clazz);

			bindConstant("config", clazz).to(config);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
