package net.sf.opendse.optimization.constraints;

import static net.sf.opendse.optimization.encoding.variables.Variables.p;
import static net.sf.opendse.optimization.encoding.variables.Variables.var;

import java.util.List;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.optimization.SpecificationWrapper;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;

public class SpecificationRouterConstraints extends AbstractSpecificationConstraints {

	@Inject
	public SpecificationRouterConstraints(SpecificationWrapper specification) {
		super();

		Specification spec = specification.getSpecification();

		// Architecture<Resource, Link> architecture = spec.getArchitecture();
		Routings<Task, Resource, Link> routings = spec.getRoutings();

		for (Task task : routings.getTasks()) {
			Architecture<Resource, Link> routing = routings.get(task);
			for (Resource resource : routing) {
				if (!isRouted(resource, task)) {
					List<DirectedLink> outLinks = Models.getOutLinks(routing, resource);
					List<DirectedLink> inLinks = Models.getInLinks(routing, resource);

					for (DirectedLink in : inLinks) {
						for (DirectedLink out : outLinks) {
							if (in.getSource().equals(out.getDest())) {
								Constraint constraint = new Constraint("<=", 1);
								constraint.add(p(var(task, in)));
								constraint.add(p(var(task, out)));
								constraints.add(constraint);
							}
						}
					}
				}
			}
		}
	}

	public static boolean isRouted(Resource resource, Task task) {
		String rString = resource.getAttribute(ROUTER);
		if (rString == null) {
			return true;
		} else {
			String[] parts = rString.trim().split(",");
			return isRouted(parts, task);
		}
	}

	protected static boolean isRouted(String[] parts, Task task) {
		String id = task.getId();

		for (String part : parts) {
			part = part.trim();
			if (part.endsWith("*")) {
				part = part.substring(0, part.length() - 1);
				if (id.startsWith(part)) {
					return true;
				}
			} else if (id.equals(part)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void doInterpreting(Specification implementation, Model model) {
		// void
	}

}
