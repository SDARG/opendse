package net.sf.opendse.encoding.application;

import java.util.HashMap;
import java.util.Map;

import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;

public class DefaultConstraintGeneratorManager implements ApplicationConstraintGeneratorManager {

	protected final Map<ActivationModes, ApplicationModeConstraintGenerator> generatorMap;

	public DefaultConstraintGeneratorManager() {
		this.generatorMap = new HashMap<ActivationModes, ApplicationModeConstraintGenerator>();
		fillGeneratorMap();
	}

	protected void fillGeneratorMap() {
		generatorMap.put(ActivationModes.STATIC, new StaticModeConstraintGenerator());
	}

	@Override
	public ApplicationModeConstraintGenerator getConstraintGenerator(ActivationModes activationMode) {
		if (!generatorMap.containsKey(activationMode)) {
			throw new IllegalArgumentException("Unknown activation mode " + activationMode);
		} else {
			return generatorMap.get(activationMode);
		}
	}
}
