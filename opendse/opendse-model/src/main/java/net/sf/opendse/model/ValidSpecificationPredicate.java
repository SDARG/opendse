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
