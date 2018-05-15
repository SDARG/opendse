package net.sf.opendse.io;

import org.junit.Assert;
import org.junit.Test;

import net.sf.opendse.io.TGFFReader;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.ResourceTypes;
import net.sf.opendse.model.Task;


public class TGFFReaderTest {	
	
	private final String testFile = "specs/opendse_example.tgff";
	
	@Test
	public void applicationTest() {
		
		TGFFReader reader = new TGFFReader(testFile);
		Application <Task, Dependency> application = reader.getApplication();
		
		Assert.assertNotNull(application);
		
		Assert.assertNotNull(application.getVertex("t0_0_0"));
		Assert.assertNotNull(application.getVertex("t0_1_0"));
		Assert.assertNotNull(application.getVertex("t0_2_0"));
		Assert.assertNotNull(application.getVertex("t0_3_0"));
		
		Assert.assertNotNull(application.getVertex("a0_0"));
		Assert.assertNotNull(application.getVertex("a0_1"));
		Assert.assertNotNull(application.getVertex("a0_2"));
		
		Assert.assertNotNull(application.getEdge("a0_0_0"));
		Assert.assertNotNull(application.getEdge("a0_0_1"));
		Assert.assertNotNull(application.getEdge("a0_1_0"));
		Assert.assertNotNull(application.getEdge("a0_1_1"));
		Assert.assertNotNull(application.getEdge("a0_2_0"));
		Assert.assertNotNull(application.getEdge("a0_2_1"));
	}
	
	@Test
	public void compTaskAttributeTest() {
		
		TGFFReader reader = new TGFFReader(testFile);
		Task task = (reader.getApplication()).getVertex("t0_3_0");
		
		Assert.assertEquals(300.0, task.getAttribute(TGFFReader.PERIOD));
		Assert.assertEquals(200.0, task.getAttribute(TGFFReader.HARD_DEADLINE));
		Assert.assertEquals("2", task.getAttribute(TGFFReader.TGFF_TYPE));		
	}
	
	@Test
	public void commTaskAttributeTest() {
		
		TGFFReader reader = new TGFFReader(testFile);
		Communication communication = (Communication)(reader.getApplication()).getVertex("a0_0");
		
		Assert.assertEquals(300.0, communication.getAttribute(TGFFReader.PERIOD));
		Assert.assertEquals("0", communication.getAttribute(TGFFReader.TGFF_TYPE));		
		Assert.assertEquals(5.0, communication.getAttribute(TGFFReader.MSG_SIZE));
	}
	
	@Test
	public void resourceTypesTest() {
		
		TGFFReader reader = new TGFFReader(testFile);
		ResourceTypes<?> resourceTypes = reader.getResourceTypes();
		
		for (int i = 0; i <= 2; i++) {
			Assert.assertNotNull(resourceTypes.get("r" + i));
		}	
		
		Assert.assertNotNull(reader.getWiring());
	}
	
	@Test
	public void resourceAttributeTest() {
		
		TGFFReader reader = new TGFFReader(testFile);
		Resource resource = (reader.getResourceTypes()).get("r0");
		
		Assert.assertEquals("79.0597", resource.getAttribute("price"));
		Assert.assertEquals("0.219023", resource.getAttribute("area"));
	}
	
	@Test
	public void mappingsTest() {
		
		TGFFReader reader = new TGFFReader(testFile);
		Mappings<Task, Resource> mappings = reader.getMappings();
		
		Task task = (reader.getApplication()).getVertex("t0_0_0");		
		Assert.assertEquals(1, (mappings.get(task)).size());
		
		task = (reader.getApplication()).getVertex("t0_1_0");		
		Assert.assertEquals(3, (mappings.get(task)).size());
		
		task = (reader.getApplication()).getVertex("t0_2_0");		
		Assert.assertEquals(3, (mappings.get(task)).size());
		
		task = (reader.getApplication()).getVertex("t0_3_0");		
		Assert.assertEquals(3, (mappings.get(task)).size());
	}
}
