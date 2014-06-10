package net.sf.opendse.optimization.test;

import net.sf.opendse.io.SpecificationReader;
import net.sf.opendse.model.Specification;
import net.sf.opendse.visualization.SpecificationViewer;

public class TesterReadFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpecificationReader reader = new SpecificationReader();
		Specification spec = reader.read("specs/spec0002.xml");
		SpecificationViewer.view(spec);
		
	}

}
