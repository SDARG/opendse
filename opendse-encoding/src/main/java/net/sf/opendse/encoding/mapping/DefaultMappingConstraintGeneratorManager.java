package net.sf.opendse.encoding.mapping;

import java.util.HashMap;
import java.util.Map;

import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;

public class DefaultMappingConstraintGeneratorManager implements MappingConstraintGeneratorManager {

	protected final Map<MappingModes, MappingConstraintGenerator> generatorMap;
	
	public DefaultMappingConstraintGeneratorManager() {
		this.generatorMap = makeGeneratorMap();
	}
	
	@Override
	public MappingConstraintGenerator getMappingConstraintGenerator(MappingModes mappingMode) {
		return generatorMap.get(mappingMode);
	}
	
	protected Map<MappingModes, MappingConstraintGenerator> makeGeneratorMap(){
		Map<MappingModes, MappingConstraintGenerator> result = new HashMap<MappingModes, MappingConstraintGenerator>();
		result.put(MappingModes.DESIGNER, new DesignerMappingsConstraintGenerator());
		return result;
	}
}
