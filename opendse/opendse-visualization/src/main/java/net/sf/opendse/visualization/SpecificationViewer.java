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
