package net.sf.opendse.encoding.mapping;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;

public class DefaultMappingConstraintGeneratorManagerTest {

	@Test
	public void test() {
		MappingConstraintManagerDefault generatorManager = new MappingConstraintManagerDefault();
		MappingConstraintGenerator generator = generatorManager.getMappingConstraintGenerator(MappingModes.DESIGNER);
		assertTrue((generator instanceof MappingConstraintGeneratorDesigner));
	}

}
