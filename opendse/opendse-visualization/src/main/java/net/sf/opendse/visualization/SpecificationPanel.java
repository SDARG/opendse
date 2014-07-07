/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.visualization;

import static javax.swing.JSplitPane.HORIZONTAL_SPLIT;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import net.sf.opendse.io.SpecificationReader;
import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

import org.opt4j.core.config.Icons;

public class SpecificationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	protected JToolBar bar = null;

	public SpecificationPanel(final Specification specification) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setSpecification(specification);
	}

	protected void setSpecification(final Specification specification) {
		this.removeAll();

		//Application<Task, Dependency> application = specification.getApplication();
		//Architecture<Resource, Link> architecture = specification.getArchitecture();
		Mappings<Task, Resource> mappings = specification.getMappings();

		ElementSelection selection = new ElementSelection();

		GraphPanel applicationPanel = new GraphPanel(new GraphPanelFormatApplication(specification, selection),
				selection);
		GraphPanel architecturePanel = new GraphPanel(new GraphPanelFormatArchitecture(specification, selection),
				selection);
		MappingPanel mappingPanel = new MappingPanel(mappings, selection);

		JSplitPane splitG = new JSplitPane(HORIZONTAL_SPLIT, applicationPanel, architecturePanel);
		JSplitPane split = new JSplitPane(HORIZONTAL_SPLIT, mappingPanel, splitG);

		this.setLayout(new BorderLayout());
		this.add(split);

		bar = new JToolBar();
		bar.setFloatable(false);
		this.add(BorderLayout.NORTH, bar);

		JButton save = new JButton("Save to ...", Icons.getIcon(Icons.DISK));
		save.setFocusable(false);
		bar.add(save);

		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();

				int returnVal = fileChooser.showDialog(SpecificationPanel.this, "save to");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					File file = fileChooser.getSelectedFile();
					SpecificationWriter writer = new SpecificationWriter();
					try {
						writer.write(specification, new FileOutputStream(file));
						System.out.println("Specification succesfully saved to " + file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}

			}
		});

		JButton open = new JButton("Open ... ", Icons.getIcon(Icons.FOLDER));
		open.setFocusable(false);
		bar.add(open);

		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();

				int returnVal = fileChooser.showDialog(SpecificationPanel.this, "open");

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					File file = fileChooser.getSelectedFile();
					SpecificationReader reader = new SpecificationReader();
					try {
						Specification spec = reader.read(new FileInputStream(file));
						setSpecification(spec);
						System.out.println("Specification succesfully loaded from " + file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}

			}
		});

		bar.setToolTipText(ViewUtil.getTooltip(specification));

		revalidate();
		repaint();
	}

}
