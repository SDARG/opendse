package net.sf.opendse.tutorial;

import java.io.File;

import net.sf.opendse.io.TgffReader;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.visualization.SpecificationViewer;



public class TgffTest {

	public static void main(String[] args) throws Exception {
     
		TgffReader reader = new TgffReader("specs/e3s-0.9/auto-indust-cowls.tgff");
		
		Application <Task, Dependency> app = reader.getApplication();
		Mappings <Task, Resource> mappings = reader.getMappings();
		Architecture <Resource, Link> arch = reader.getArchitecture();
		
		Specification spec = new Specification(app, arch, mappings);
		
		SpecificationViewer.view(spec);
	
	}

}
