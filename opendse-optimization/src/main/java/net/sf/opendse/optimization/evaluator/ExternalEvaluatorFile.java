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

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Specification;

import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;

public class ExternalEvaluatorFile extends AbstractExternalEvaluator {


	public ExternalEvaluatorFile(String command, int priority) {
		super(command, priority);
		
		try {
			File file = File.createTempFile("eval", ".xml");
			File result = File.createTempFile("result", ".xml");
			
			//breaking command string into tokens for using the recommended exec function which needs a string[]
			String temp = command+" "+file.getAbsolutePath()+" "+result.getAbsolutePath();
			StringTokenizer tokenizer = new StringTokenizer(temp);
			String[] commandArray = new String[tokenizer.countTokens()];
			int counter = 0;
			while(tokenizer.hasMoreTokens()) {
				commandArray[counter] = tokenizer.nextToken();
				counter++;
			}
			
			Process process = Runtime.getRuntime().exec(commandArray);
			process.waitFor();
			
			ResultElement resultElement = getResultElement(result);
			
			for (ObjectiveElement objectiveElement : resultElement.getObjectiveElements()) {
				Objective objective = toObjective(objectiveElement);
				objectiveMap.put(objective.getName(), objective);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	public Specification evaluate(Specification implementation, Objectives objectives) {
		try {
			File file = File.createTempFile("eval", ".xml");
			File result = File.createTempFile("result", ".xml");
			
			SpecificationWriter writer = new SpecificationWriter();
			writer.write(implementation, file);
			
			//breaking command string into tokens for using the recommended exec function which needs a string[]
			String temp = command+" "+file.getAbsolutePath()+" "+result.getAbsolutePath();
			StringTokenizer tokenizer = new StringTokenizer(temp);
			String[] commandArray = new String[tokenizer.countTokens()];
			int counter = 0;
			while(tokenizer.hasMoreTokens()) {
				commandArray[counter] = tokenizer.nextToken();
				counter++;
			}
			
			Process process = Runtime.getRuntime().exec(commandArray);
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			ResultElement resultElement = getResultElement(result);

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
