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
