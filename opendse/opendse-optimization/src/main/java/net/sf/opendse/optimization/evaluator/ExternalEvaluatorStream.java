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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.ImplementationEvaluator;

import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;

public class ExternalEvaluatorStream extends AbstractExternalEvaluator implements ImplementationEvaluator {

	protected boolean isInit = false;

	public ExternalEvaluatorStream(String command, int priority) {
		super(command, priority);
	}

	private synchronized void init() {
		if (!isInit) {
			try {
				Process process = Runtime.getRuntime().exec(command);

				final InputStream in = process.getInputStream();
				final InputStream err = process.getErrorStream();
				final OutputStream out = process.getOutputStream();

				Thread errorReader = new ErrorThread(err);
				errorReader.start();

				out.close();
				ResultElement resultElement = getResultElement(in);
				in.close();
				for (ObjectiveElement objectiveElement : resultElement.getObjectiveElements()) {
					Objective objective = toObjective(objectiveElement);
					objectiveMap.put(objective.getName(), objective);
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Specification evaluate(Specification implementation, Objectives objectives) {
		if (!isInit) {
			init();
		}

		try {
			Process process = Runtime.getRuntime().exec(command);
			final InputStream in = process.getInputStream();
			final InputStream err = process.getErrorStream();
			final OutputStream out = process.getOutputStream();

			Thread errorReader = new ErrorThread(err);
			errorReader.start();

			SpecificationWriter writer = new SpecificationWriter();
			writer.write(implementation, out);
			out.close();

			ResultElement resultElement = getResultElement(in);
			in.close();

			for (ObjectiveElement objectiveElement : resultElement.getObjectiveElements()) {
				Objective objective = objectiveMap.get(objectiveElement.getName());
				objectives.add(objective, objectiveElement.getValue());
			}

			Specification spec = resultElement.getSpecification();

			return spec;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
