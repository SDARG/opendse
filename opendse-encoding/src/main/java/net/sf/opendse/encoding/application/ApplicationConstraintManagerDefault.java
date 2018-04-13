package net.sf.opendse.encoding.application;

import java.util.HashMap;
import java.util.Map;

import net.sf.opendse.model.properties.ApplicationElementPropertyService.ActivationModes;

public class ApplicationConstraintManagerDefault implements ApplicationConstraintManager {

	protected final Map<ActivationModes, ApplicationConstraintGenerator> generatorMap;

	public ApplicationConstraintManagerDefault() {
		this.generatorMap = new HashMap<ActivationModes, ApplicationConstraintGenerator>();
		fillGeneratorMap();
	}

	protected void fillGeneratorMap() {
		generatorMap.put(ActivationModes.STATIC, new ApplicationConstraintGeneratorStatic());
		generatorMap.put(ActivationModes.ALTERNATIVE, new ApplicationConstraintGeneratorAlternative());
	}

	@Override
	public ApplicationConstraintGenerator getConstraintGenerator(ActivationModes activationMode) {
		if (!generatorMap.containsKey(activationMode)) {
			throw new IllegalArgumentException("Unknown activation mode " + activationMode);
		} else {
			return generatorMap.get(activationMode);
		}
	}
}
