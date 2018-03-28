package net.sf.opendse.encoding.allocation;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.AllocationEncoding;
import net.sf.opendse.encoding.MappingEncoding;
import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Specification;

/**
 * 
 * The {@link UtilizationAllocationEncoding} formulates the {@link Constraint}
 * that ensure that the utilized parts of the {@link Specification}
 * {@link Architecture}, that is the {@link Resource} used as binding targets of
 * {@link Mapping}s and the {@link Link}s used for the routing of
 * {@link Communication}s are allocated. Resources that are not utilized are not
 * included into the implementation {@link Specification}.
 * 
 * @author Fedor Smirnov
 *
 */
public class UtilizationAllocationEncoding implements AllocationEncoding {

	@Override
	public Set<Constraint> toConstraints(Set<MappingVariable> mappingVariables, Set<RoutingVariable> routingVariables,
			Architecture<Resource, Link> architecture) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the {@link Set} of {@link Variable}s that are relevant for the
	 * allocation of the given {@link Resource}. A variable is relevant if its
	 * activation automatically results in the activation of the current resource.
	 * 
	 * @param resource
	 *            the current resource
	 * @param mappingVariables
	 *            the set of {@link MappingVariable}s encoded by the
	 *            {@link MappingEncoding}
	 * @param routingVariables
	 *            the set of {@link RoutingVariable}s encoded by the
	 *            {@link RoutingEncoding}
	 * @return the {@link Set} of {@link Variable}s that are relevant for the
	 *         allocation of the given {@link Resource}. A variable is relevant if
	 *         its activation automatically results in the activation of the current
	 *         resource
	 */
	protected Set<Variable> gatherRelevantVariables(Resource resource, Set<MappingVariable> mappingVariables,
			Set<RoutingVariable> routingVariables) {
		Set<Variable> relevantVariables = new HashSet<Variable>();
		for (MappingVariable mappingVar : mappingVariables) {
			if (mappingVar instanceof M) {
				M mVar = (M) mappingVar;
				if (mVar.getMapping().getTarget().equals(resource)) {
					relevantVariables.add(mVar);
				}
			}
		}
		for (RoutingVariable routingVar : routingVariables) {
			if (routingVar instanceof CR) {
				CR crVar = (CR) routingVar;
				if (crVar.getResource().equals(resource)) {
					relevantVariables.add(crVar);
				}
			}else if (routingVar instanceof CLRR) {
				CLRR clrrVar = (CLRR) routingVar;
				if (clrrVar.getSource().equals(resource) || clrrVar.getDestination().equals(resource)) {
					relevantVariables.add(clrrVar);
				}
			}
		}
		return relevantVariables;
	}
}
