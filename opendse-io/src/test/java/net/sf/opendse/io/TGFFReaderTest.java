package net.sf.opendse.io;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import net.sf.opendse.io.TGFFReader;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;


public class TGFFReaderTest {	
	
	@Test
	public void applicationTest() {
		
		TGFFReader reader = new TGFFReader("specs/e3s-0.9/office-automation-mocsyn.tgff");
		Application <Task, Dependency> application = reader.getApplication();
		
		Assert.assertNotNull(application);
		
		Assert.assertNotNull(application.getVertex("src_0"));
		Assert.assertNotNull(application.getVertex("text_0"));
		Assert.assertNotNull(application.getVertex("sink_0"));
		Assert.assertNotNull(application.getVertex("rotate_0"));
		Assert.assertNotNull(application.getVertex("dith_0"));
		
		Assert.assertNotNull(application.getVertex("a0_0"));
		Assert.assertNotNull(application.getVertex("a0_1"));
		Assert.assertNotNull(application.getVertex("a0_2"));
		Assert.assertNotNull(application.getVertex("a0_3"));
		Assert.assertNotNull(application.getVertex("a0_4"));
		
		Assert.assertNotNull(application.getEdge("a0_0_0"));
		Assert.assertNotNull(application.getEdge("a0_0_1"));
		Assert.assertNotNull(application.getEdge("a0_1_0"));
		Assert.assertNotNull(application.getEdge("a0_1_1"));
		Assert.assertNotNull(application.getEdge("a0_2_0"));
		Assert.assertNotNull(application.getEdge("a0_2_1"));
		Assert.assertNotNull(application.getEdge("a0_3_0"));
		Assert.assertNotNull(application.getEdge("a0_3_1"));
		Assert.assertNotNull(application.getEdge("a0_4_0"));
		Assert.assertNotNull(application.getEdge("a0_4_1"));		
	}
	
	@Test
	public void compTaskAttributeTest() {
		
		TGFFReader reader = new TGFFReader("specs/e3s-0.9/office-automation-mocsyn.tgff");
		Task task = (reader.getApplication()).getVertex("sink_0");
		
		Assert.assertEquals(0.03, task.getAttribute(TGFFReader.PERIOD));
		Assert.assertEquals(0.4, task.getAttribute(TGFFReader.HARD_DEADLINE));
		Assert.assertEquals("45", task.getAttribute(TGFFReader.TGFF_TYPE));		
	}
	
	@Test
	public void commTaskAttributeTest() {
		
		TGFFReader reader = new TGFFReader("specs/e3s-0.9/office-automation-mocsyn.tgff");
		Communication communication = (Communication)(reader.getApplication()).getVertex("a0_0");
		
		Assert.assertEquals(0.03, communication.getAttribute(TGFFReader.PERIOD));
		Assert.assertEquals("0", communication.getAttribute(TGFFReader.TGFF_TYPE));		
		Assert.assertEquals(1000.0, communication.getAttribute(TGFFReader.MSG_SIZE));
	}
	
	@Test
	public void architectureTest() {
		
		TGFFReader reader = new TGFFReader("specs/e3s-0.9/office-automation-mocsyn.tgff");
		Architecture<?, ?> architecture = reader.getArchitecture();
		
		for (int i = 0; i < 34; i++) {
			Assert.assertNotNull(architecture.getVertex("r" + i));
		}	
		
		Assert.assertNotNull(architecture.getVertex("@WIRING"));
	}
	
	@Test
	public void resourceAttributeTest() {
		
		TGFFReader reader = new TGFFReader("specs/e3s-0.9/office-automation-mocsyn.tgff");
		Resource resource = (reader.getArchitecture()).getVertex("r0");
		
		Assert.assertEquals("33", resource.getAttribute("price"));
		Assert.assertEquals("1", resource.getAttribute("buffered"));
		Assert.assertEquals("1.33e+08", resource.getAttribute("max_freq"));
		Assert.assertEquals("3.10e-03", resource.getAttribute("width"));
		Assert.assertEquals("3.10e-03", resource.getAttribute("height"));
		Assert.assertEquals("0.275", resource.getAttribute("density"));
		Assert.assertEquals("0", resource.getAttribute("preempt_power"));
		Assert.assertEquals("0", resource.getAttribute("commun_en_bit"));
		Assert.assertEquals("0", resource.getAttribute("io_en_bit"));
		Assert.assertEquals("0.16", resource.getAttribute("idle_power"));
	}
	
	@Test
	public void mappingsTest() {
		
		TGFFReader reader = new TGFFReader("specs/e3s-0.9/office-automation-mocsyn.tgff");
		Mappings<Task, Resource> mappings = reader.getMappings();
		
		Task task = (reader.getApplication()).getVertex("src_0");		
		Assert.assertEquals(34, (mappings.get(task)).size());
		
		task = (reader.getApplication()).getVertex("text_0");		
		Assert.assertEquals(18, (mappings.get(task)).size());
		
		task = (reader.getApplication()).getVertex("sink_0");		
		Assert.assertEquals(34, (mappings.get(task)).size());
		
		task = (reader.getApplication()).getVertex("rotate_0");		
		Assert.assertEquals(18, (mappings.get(task)).size());
		
		task = (reader.getApplication()).getVertex("dith_0");		
		Assert.assertEquals(18, (mappings.get(task)).size());
	}
}
