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
package net.sf.opendse.model;

import org.apache.commons.collections15.Predicate;

/**
 * The {@code ValidImplementationPredicate} is a {@code Predicate} that returns
 * {@code true} if the {@link Specification} is a valid specification.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class ValidSpecificationPredicate implements Predicate<Specification> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.commons.collections15.Predicate#evaluate(java.lang.Object)
	 */
	@Override
	public boolean evaluate(Specification specification) {
		Application<Task, Dependency> application = specification.getApplication();
		Architecture<Resource, Link> architecture = specification.getArchitecture();
		Mappings<Task, Resource> mappings = specification.getMappings();

		boolean valid = true;

		for (Mapping<Task, Resource> mapping : mappings) {

			Task source = mapping.getSource();
			Resource target = mapping.getTarget();

			if (!application.containsVertex(source)) {
				System.out.println("Mapping " + mapping + " from task " + source
						+ ": Task does not exist in application");
				valid = false;
			}
			if (!architecture.containsVertex(target)) {
				System.out.println("Mapping " + mapping + " to resource " + target
						+ ": Resource does not exist in architecture");
				valid = false;
			}

		}

		return valid;
	}
}
