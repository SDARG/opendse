package net.sf.opendse.optimization.evaluator;

import java.io.File;
import java.io.IOException;

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
			
			Process process = Runtime.getRuntime().exec(command+" "+file.getAbsolutePath()+" "+result.getAbsolutePath());
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
			
			Process process = Runtime.getRuntime().exec(command+" "+file.getAbsolutePath()+" "+result.getAbsolutePath());
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
