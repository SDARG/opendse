/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.encoding.constraints;

import static net.sf.opendse.encoding.old.variables.Variables.p;
import static net.sf.opendse.encoding.old.variables.Variables.var;

import java.util.List;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.SpecificationWrapper;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

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
