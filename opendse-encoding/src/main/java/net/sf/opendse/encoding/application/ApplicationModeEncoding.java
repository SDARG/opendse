package net.sf.opendse.encoding.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import com.google.inject.Inject;

import net.sf.opendse.encoding.ApplicationEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ApplicationElementPropertyService;
import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;

/**
 * The {@link ApplicationModeEncoding} is an {@link ApplicationEncoding} that
 * enables the usage of different {@link ApplicationModeConstraintGenerator} to
 * process different parts of the application.
 * 
 * @author Fedor Smirnov
 *
 */
public class ApplicationModeEncoding implements ApplicationEncoding {

	protected final ApplicationConstraintGeneratorManager generatorManager;

	@Inject
	public ApplicationModeEncoding(ApplicationConstraintGeneratorManager generatorManager) {
		this.generatorManager = generatorManager;
	}

	@Override
	public Set<Constraint> toConstraints(Application<Task, Dependency> application) {
		Set<Constraint> applicationConstraints = new HashSet<Constraint>();
		Map<ActivationModes, Set<ApplicationVariable>> applicationModeMap = filterApplicationModes(application);
		// generate the constraints for each mode
		for (Entry<ActivationModes, Set<ApplicationVariable>> entry : applicationModeMap.entrySet()) {
			ActivationModes activationMode = entry.getKey();
			Set<ApplicationVariable> variables = entry.getValue();
			ApplicationModeConstraintGenerator constraintGenerator = generatorManager
					.getConstraintGenerator(activationMode);
			applicationConstraints.addAll(constraintGenerator.toConstraints(variables));
		}
		return applicationConstraints;
	}

	/**
	 * Filters the tasks and dependencies according to their activation modes.
	 * 
	 * @param application
	 *            the application graph
	 * @return map where the activation modes are mapped onto the sets of their
	 *         application variables
	 */
	protected Map<ActivationModes, Set<ApplicationVariable>> filterApplicationModes(
			Application<Task, Dependency> application) {
		Map<ActivationModes, Set<ApplicationVariable>> result = new HashMap<ActivationModes, Set<ApplicationVariable>>();
		// process the tasks
		for (Task task : application) {
			ActivationModes activationMode = ApplicationElementPropertyService.getActivationMode(task);
			if (!result.containsKey(activationMode)) {
				result.put(activationMode, new HashSet<ApplicationVariable>());
			}
			result.get(activationMode).add(Variables.varT(task));
		}
		// process the dependencies
		for (Dependency dependency : application.getEdges()) {
			ActivationModes activationMode = ApplicationElementPropertyService.getActivationMode(dependency);
			if (!result.containsKey(activationMode)) {
				result.put(activationMode, new HashSet<ApplicationVariable>());
			}
			Task source = application.getSource(dependency);
			Task destination = application.getDest(dependency);
			result.get(activationMode).add(Variables.varDTT(dependency, source, destination));
		}
		return result;
	}
}
