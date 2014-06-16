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
package net.sf.opendse.optimization.constraints;

import static net.sf.opendse.optimization.encoding.variables.Variables.p;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;
import org.opt4j.satdecoding.Term;

import com.google.inject.Inject;

public class SpecificationConnectConstraints extends AbstractSpecificationConstraints implements
		SpecificationConstraints {

	protected final Specification specification;
	protected final Set<ConnectConstraint> connectConstraints = new HashSet<ConnectConstraint>();

	@Inject
	public SpecificationConnectConstraints(SpecificationWrapper specification) {
		super();
		this.specification = specification.getSpecification();
		this.connectConstraints.addAll(getConnectConstraints(this.specification));
		
		initConstraints();
	}

	public static boolean isConnect(String attributeName) {
		return attributeName.endsWith(CONNECT);
	}

	public static boolean isConnectMax(String attributeName) {
		return attributeName.endsWith(CONNECT_MAX);
	}

	public static boolean isConnectMin(String attributeName) {
		return attributeName.endsWith(CONNECT_MIN);
	}

	public static String getName(String attributeName) {
		return attributeName.split(":")[0];
	}

	public static class ConnectConstraint {

		protected final Resource resource;
		protected final String attribute;
		protected final Set<Link> links;

		public ConnectConstraint(Resource resource, String attribute, Set<Link> links) {
			super();
			this.resource = resource;
			this.attribute = attribute;
			this.links = links;
		}

		public Resource getResource() {
			return resource;
		}

		public String getAttribute() {
			return attribute;
		}

		public Set<Link> getLinks() {
			return links;
		}

	}

	public static Set<ConnectConstraint> getConnectConstraints(Specification specification) {
		Set<ConnectConstraint> set = new HashSet<ConnectConstraint>();
		Architecture<Resource, Link> architecture = specification.getArchitecture();
		
		for (Resource resource : architecture) {

			Set<String> attributeNamesTested = new HashSet<String>();
			
			for (String attributeName : resource.getAttributeNames()) {

				if (isConnectMax(attributeName) || isConnectMin(attributeName) || isConnect(attributeName)) {
					String name = getName(attributeName);
					if (!attributeNamesTested.contains(name)) {
						attributeNamesTested.add(name);

						Set<Link> links = new HashSet<Link>();

						String[] parts = name.split("\\|");

						for (Link link : architecture.getIncidentEdges(resource)) {
							String type = link.getType();

							if (type != null) {
								for (String part : parts) {
									if (type.startsWith(part)) {
										links.add(link);
										break;
									}
								}
							}
						}

						ConnectConstraint connectConstraint = new ConnectConstraint(resource, name, links);
						set.add(connectConstraint);
					}
				}
			}
		}
		return set;
	}

	public void initConstraints() {
		for (ConnectConstraint connectConstraint : connectConstraints) {
			Resource resource = connectConstraint.getResource();
			String attribute = connectConstraint.getAttribute();			
			Set<Link> links = connectConstraint.getLinks();
			
			
			ParameterObject con = getParameter(resource, attribute + CONNECT);
			ParameterObject min = getParameter(resource, attribute + CONNECT_MIN);
			ParameterObject max = getParameter(resource, attribute + CONNECT_MAX);
			
			if(min.isNull() && !con.isNull()){
				min = con;
			}
			if(max.isNull() && !con.isNull()){
				max = con;
			}
			
			List<Term> linkTerms = new ArrayList<Term>();
			
			for(Link link: links){
				linkTerms.add(new Term(1, p(link)));
			}
			
			if(!max.isNull()){
				Constraint cmax = new Constraint("<=", 0);
				cmax.addAll(linkTerms);

				if (!max.isParameter()) {
					cmax.setRhs(max.getInteger());
				} else {
					Constraint eq = new Constraint("=", 0);
					for (Term term : getParameterTerms(resource, max)) {
						Term t = new Term(-term.getCoefficient(), term.getLiteral());
						cmax.add(t);
						eq.add(1, term.getLiteral());
					}
					eq.add(-1, p(resource));
					constraints.add(eq);
				}
				constraints.add(cmax);
			}
			
			if (!min.isNull()) {
				Constraint cmin = new Constraint(">=", 0);
				cmin.addAll(linkTerms);

				if (!min.isParameter()) {
					cmin.add(-min.getInteger(), p(resource));
				} else {
					Constraint eq = new Constraint("=", 0);
					for (Term term : getParameterTerms(resource, min)) {
						Term t = new Term(-term.getCoefficient(), term.getLiteral());
						cmin.add(t);
						eq.add(1, term.getLiteral());
					}
					eq.add(-1, p(resource));
					constraints.add(eq);
				}
				constraints.add(cmin);
			}
		}
	}

	@Override
	public void doInterpreting(Specification implementation, Model model) {
		/*
		for (ConnectConstraint connectConstraint : getConnectConstraints(implementation)) {
			Resource resource = connectConstraint.getResource();
			String attribute = connectConstraint.getAttribute();	

			ParameterObject con = getParameter(resource.getParent(), attribute + CONNECT);
			ParameterObject min = getParameter(resource.getParent(), attribute + CONNECT_MIN);
			ParameterObject max = getParameter(resource.getParent(), attribute + CONNECT_MAX);

			if(min.isNull() && !con.isNull()){
				min = con;
			}
			if(max.isNull() && !con.isNull()){
				max = con;
			}
			
			// System.err.println(ViewUtil.getTooltip(element));
			if (!max.isNull() && max.isParameter()) {
				Set<Term> terms = getParameterTerms(resource.getParent(), max);
				for (Term term : terms) {
					if (model.get(term.getLiteral().variable())) {
						resource.setAttribute(attribute + CONNECT_MAX, term.getCoefficient());
					}
				}
			}
			if (!min.isNull() && min.isParameter()) {
				Set<Term> terms = getParameterTerms(resource.getParent(), min);
				for (Term term : terms) {
					if (model.get(term.getLiteral().variable())) {
						resource.setAttribute(attribute + CONNECT_MIN, term.getCoefficient());
					}
				}
			}


		}
		*/
	}

}
