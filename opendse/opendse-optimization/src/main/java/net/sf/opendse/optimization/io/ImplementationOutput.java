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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.ImplementationWrapper;

import org.opt4j.core.Individual;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.core.optimizer.OptimizerStateListener;
import org.opt4j.core.start.Constant;

import com.google.inject.Inject;

public class ImplementationOutput implements OptimizerStateListener {

	protected final Archive archive;
	protected final String filename;

	@Inject
	public ImplementationOutput(Archive archive,
			@Constant(namespace = ImplementationOutput.class, value = "filename") String filename) {
		super();
		this.archive = archive;
		this.filename = filename;
	}

	@Override
	public void optimizationStarted(Optimizer optimizer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void optimizationStopped(Optimizer optimizer) {
		File file = new File(filename);
		try {
			FileOutputStream out = new FileOutputStream(file);

			Set<Specification> implementations = new HashSet<Specification>();
			for (Individual individual : archive) {
				ImplementationWrapper wrapper = (ImplementationWrapper) individual.getPhenotype();
				implementations.add(wrapper.getImplementation());
			}
			SpecificationWriter writer = new SpecificationWriter();
			writer.write(implementations, out);
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
