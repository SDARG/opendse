package net.sf.opendse.optimization.modifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

public class RedundantCommunicationModifier implements Modifier {

	@Override
	public void modify(Specification specification) {
		Application<Task, Dependency> application = specification.getApplication();
		Mappings<Task, Resource> mappings = specification.getMappings();

		Set<Task> removeM = new HashSet<Task>();
		for (Task m : Models.filterCommunications(application)) {
			Collection<Task> pred = application.getPredecessors(m);
			Collection<Task> succ = application.getSuccessors(m);

			for (Task ti : pred) {
				for (Task tj : succ) {
					if (mappings.getTargets(ti).equals(mappings.getTargets(tj))) {
						Dependency d = application.findEdge(m, tj);
						application.removeEdge(d);
						application.addEdge(d, ti, tj);
					}
				}
			}
			if (application.getSuccessorCount(m) == 0) {
				removeM.add(m);
			}
		}

		for (Task m : removeM) {
			application.removeVertex(m);
		}
	}

}
