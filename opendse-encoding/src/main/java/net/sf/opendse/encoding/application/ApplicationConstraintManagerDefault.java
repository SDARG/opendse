package net.sf.opendse.encoding.application;

import java.util.HashMap;
import java.util.Map;

import net.sf.opendse.model.properties.ApplicationElementPropertyService;

public class ApplicationConstraintManagerDefault implements ApplicationConstraintManager {

	protected final Map<String, ApplicationConstraintGenerator> generatorMap;

	public ApplicationConstraintManagerDefault() {
		this.generatorMap = new HashMap<String, ApplicationConstraintGenerator>();
		fillGeneratorMap();
	}

	protected void fillGeneratorMap() {
		generatorMap.put(ApplicationElementPropertyService.activationAttributeStatic, new ApplicationConstraintGeneratorStatic());
		generatorMap.put(ApplicationElementPropertyService.activationAttributeAlternative, new ApplicationConstraintGeneratorAlternative());
	}

	@Override
	public ApplicationConstraintGenerator getConstraintGenerator(String activationMode) {
		if (!generatorMap.containsKey(activationMode)) {
			throw new IllegalArgumentException("Unknown activation mode " + activationMode);
		} else {
			return generatorMap.get(activationMode);
		}
	}
}
