package net.sf.opendse.encoding.mapping;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;

public class DefaultMappingConstraintGeneratorManagerTest {

	@Test
	public void test() {
		DefaultMappingConstraintGeneratorManager generatorManager = new DefaultMappingConstraintGeneratorManager();
		MappingConstraintGenerator generator = generatorManager.getMappingConstraintGenerator(MappingModes.DESIGNER);
		assertTrue((generator instanceof DesignerMappingsConstraintGenerator));
	}

}
