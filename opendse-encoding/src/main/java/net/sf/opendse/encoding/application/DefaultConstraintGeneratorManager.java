package net.sf.opendse.encoding.application;

import java.util.HashMap;
import java.util.Map;

import net.sf.opendse.model.properties.TaskPropertyService;

public class DefaultConstraintGeneratorManager implements ApplicationConstraintGeneratorManager {

	protected final Map<String, ApplicationModeConstraintGenerator> generatorMap;

	public DefaultConstraintGeneratorManager() {
		this.generatorMap = new HashMap<String, ApplicationModeConstraintGenerator>();
		fillGeneratorMap();
	}

	protected void fillGeneratorMap() {
		generatorMap.put(TaskPropertyService.ActivationModes.STATIC.getXmlName(), new StaticModeConstraintGenerator());
	}

	@Override
	public ApplicationModeConstraintGenerator getConstraintGenerator(String activationMode) {
		if (!generatorMap.containsKey(activationMode)) {
			throw new IllegalArgumentException("Unknown activation mode " + activationMode);
		} else {
			return generatorMap.get(activationMode);
		}
	}
}
