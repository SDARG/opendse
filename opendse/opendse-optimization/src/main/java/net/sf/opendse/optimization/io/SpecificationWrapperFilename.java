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
package net.sf.opendse.optimization.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.sf.opendse.io.SpecificationReader;

import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

public class SpecificationWrapperFilename extends SpecificationWrapperInstance {

	
	static SpecificationReader reader = new SpecificationReader();


	@Inject
	public SpecificationWrapperFilename(
			@Constant(namespace = SpecificationWrapperFilename.class, value = "filename") String filename) throws FileNotFoundException {
		super(reader.read(new FileInputStream(filename)));
	}

}
