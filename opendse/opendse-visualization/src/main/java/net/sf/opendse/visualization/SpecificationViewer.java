package net.sf.opendse.visualization;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import net.sf.opendse.model.Specification;

public class SpecificationViewer {

	public static void view(Specification specification) {
		JFrame frame = new JFrame();
		
		SpecificationPanel panel = new SpecificationPanel(specification);
		
		frame.setLayout(new BorderLayout());
		frame.add(panel);

		frame.pack();
		frame.setVisible(true);

		//frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
}
