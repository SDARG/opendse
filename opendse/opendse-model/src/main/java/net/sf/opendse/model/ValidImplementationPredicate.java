package net.sf.opendse.model;

import static net.sf.opendse.model.Models.filterCommunications;
import static net.sf.opendse.model.Models.filterProcesses;
import static net.sf.opendse.model.Models.getLinks;

import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Models.DirectedLink;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;

/**
 * The {@code ValidImplementationPredicate} is a {@code Predicate} that returns
 * {@code true} if the {@link Specification} is a valid implementation.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class ValidImplementationPredicate implements Predicate<Specification> {

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
		Routings<Task, Resource, Link> routings = specification.getRoutings();

		boolean valid = true;

		for (Task p : filterProcesses(application)) {
			Set<Mapping<Task, Resource>> set = mappings.get(p);
			if (set.size() != 1) {
				System.out.println("Process " + p + " is mapped with " + set);
				valid = false;
			} else {
				Resource r = set.iterator().next().getTarget();
				if (!architecture.containsVertex(r)) {
					System.out.println("Process " + p + " is mapped to " + r + " that is not in architecture");
				}
			}
		}

		for (Task c : filterCommunications(application)) {
			Architecture<Resource, Link> routing = routings.get(c);

			for (Resource r : routing.getVertices()) {
				if (!architecture.containsVertex(r)) {
					System.out.println("Communication " + c + "contains resource " + r
							+ " that is not in the architecture");
					valid = false;
				}
			}

			for (DirectedLink link : getLinks(routing)) {
				Link l = link.getLink();
				Resource r0 = link.getSource();
				Resource r1 = link.getDest();

				if (!architecture.isSuccessor(r0, r1) || !l.equals(architecture.findEdge(r0, r1))) {
					System.out
							.println("Communication " + c + "contains link " + l + " that is not in the architecture");
					valid = false;
				}
			}

			for (Task p : application.getNeighbors(c)) {
				Resource r = mappings.get(p).iterator().next().getTarget();
				if (!routing.containsVertex(r)) {
					System.out.println("Communication " + c + " does not contain target resource " + r + " of process "
							+ p + " in the routing");
					valid = false;
				}
			}

			Task pred = application.getPredecessors(c).iterator().next();
			Resource root = mappings.get(pred).iterator().next().getTarget();

			Set<Resource> leaves = new HashSet<Resource>();
			for (Task succ : application.getSuccessors(c)) {
				leaves.add(mappings.get(succ).iterator().next().getTarget());
			}

			boolean isTree = true;

			WeakComponentClusterer<Resource, Link> clusterer = new WeakComponentClusterer<Resource, Link>();
			Set<Set<Resource>> cluster = clusterer.transform(routing);

			if (cluster.size() != 1) {
				isTree = false;
			}

			for (Resource r : routing.getVertices()) {
				if (routing.getPredecessorCount(r) == 0) {
					if (!r.equals(root)) {
						System.out.println("Communication " + c + " originate at " + r
								+ " but predecessor task is mapped to " + root);
						valid = false;
					}
				} else if (routing.getPredecessorCount(r) > 1) {
					isTree = false;
				}

				if (routing.getSuccessorCount(r) == 0) {
					if (!leaves.contains(r)) {
						System.out.println("Communication " + c + " terminates at " + r
								+ " which is not a target of a successor resource");
					}
				}
			}

			if (!isTree) {
				System.out.println("Communication " + c + " is not routed on valid tree");
				valid = false;
			}

		}

		return valid;
	}
}
