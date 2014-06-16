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
