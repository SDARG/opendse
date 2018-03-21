package net.sf.opendse.model.properties;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;
import net.sf.opendse.model.properties.ProcessPropertyService.ProcessAttributes;

public class ProcessPropertyServiceTest {

	@Test(expected=IllegalArgumentException.class)
	public void testCommInput() {
		Communication comm = new Communication("comm");
		ProcessPropertyService.getMappingMode(comm);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUnknownMappingMode() {
		Task process = new Task("t");
		process.setAttribute(ProcessAttributes.MAPPING_MODE.getXmlName(), "invalid");
		ProcessPropertyService.getMappingMode(process);
	}
	
	@Test
	public void testGetSetMappingMode() {
		Task process = new Task("process");
		assertEquals(MappingModes.DESIGNER, ProcessPropertyService.getMappingMode(process));
		ProcessPropertyService.setMappingMode(process, MappingModes.DESIGNER);
		assertEquals(MappingModes.DESIGNER, ProcessPropertyService.getMappingMode(process));
		ProcessPropertyService.setMappingMode(process, MappingModes.TYPE);
		assertEquals(MappingModes.TYPE, ProcessPropertyService.getMappingMode(process));
	}
}
