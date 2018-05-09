package net.sf.opendse.encoding.application;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;

import net.sf.opendse.model.properties.ApplicationElementPropertyService;

@Singleton
public class ApplicationConstraintManagerDefault implements ApplicationConstraintManager {

	protected final Map<String, ApplicationConstraintGenerator> generatorMap;

	public ApplicationConstraintManagerDefault() {
		this.generatorMap = new HashMap<String, ApplicationConstraintGenerator>();
		fillGeneratorMap();
	}

	protected void fillGeneratorMap() {
		generatorMap.put(ApplicationElementPropertyService.activationAttributeStatic,
				new ApplicationConstraintGeneratorStatic());
		generatorMap.put(ApplicationElementPropertyService.activationAttributeAlternative,
				new ApplicationConstraintGeneratorAlternative());
	}

	/**
	 * Adds an additional {@link ApplicationConstraintGenerator} that is to be used
	 * to generate constraints for the application elements with the given
	 * activationMode.
	 * 
	 * @param activationMode
	 *            the activation mode of the application elements that are to be
	 *            encoded with the given {@link ApplicationConstraintGenerator}
	 * @param generator
	 *            the {@link ApplicationConstraintGenerator} that is to be used to
	 *            generate constraints for the application elements with the given
	 *            activationMode
	 */
	public void addConstraintGenerator(String activationMode, ApplicationConstraintGenerator generator) {
		generatorMap.put(activationMode, generator);
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
