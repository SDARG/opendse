package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.CRR;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

/**
 * The {@link ResourceOrderEncoder} encodes the order in which a
 * {@link Communication} is transmitted by the {@link Resource}s by generating
 * the {@link Constraint}s that encoder the {@link CRR} {@link Variable}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class ResourceOrderEncoder {

	/**
	 * Generates the {@link Constraint}s encoding the {@link CRR} {@link Variable}s.
	 * 
	 * @param communicationVariable
	 *            the {@link T} variable encoding the activation of the given
	 *            {@link Communication}
	 * @param routing
	 *            the {@link Architecture} describing the routing possibilities of
	 *            the current communication
	 * @return the {@link Constraint}s encoding the {@link CRR} {@link Variable}s
	 */
	public Set<Constraint> generateResourceOrderConstraints(T communicationVariable,
			Architecture<Resource, Link> routing) {
		Task comm = communicationVariable.getTask();
		// iterates all directed links
		Set<Constraint> result = new HashSet<Constraint>();
		for (DirectedLink dLink : Models.getLinks(routing)) {
			result.add(makeLinkActivationConstraint(comm, dLink));
		}
		for (Resource resA : routing) {
			// iterates all resource pairs
			for (Resource resB : routing) {
				if (resB.equals(resA))
					continue;
				result.addAll(makeResourcePairConstraints(comm, resA, resB));
				// iterates all resource triplets
				for (Resource resC : routing) {
					if (resC.equals(resA) || resC.equals(resB))
						continue;
				}
			}
		}

		return new HashSet<Constraint>();
	}

	/**
	 * Formulates the {@link Constraint}s stating that the order between two
	 * resource is directional and that it is not specified if one of the resources
	 * is not used.
	 * 
	 * @param comm
	 *            the communication that is being routed
	 * @param resA
	 *            one of two resources
	 * @param resB
	 *            the other one of two resource
	 * @return the {@link Constraint}s stating that the order between two resource
	 *         is directional and that it is not specified if one of the resources
	 *         is not used
	 */
	protected Set<Constraint> makeResourcePairConstraints(Task comm, Resource resA, Resource resB) {
		Set<Constraint> result = new HashSet<Constraint>();
		CR aUsed = Variables.varCR(comm, resA);
		CR bUsed = Variables.varCR(comm, resB);
		CRR aBeforeB = Variables.varCRR(comm, resA, resB);
		CRR bBeforeA = Variables.varCRR(comm, resB, resA);
		result.add(Constraints.generateNegativeImplication(aUsed, aBeforeB));
		result.add(Constraints.generateNegativeImplication(aUsed, bBeforeA));
		result.add(Constraints.generateNegativeImplication(bUsed, aBeforeB));
		result.add(Constraints.generateNegativeImplication(bUsed, bBeforeA));
		return result;
	}

	/**
	 * Generates the {@link Constraint} stating that if a directed link is
	 * activated, its source comes before it destination. If the link is unactive,
	 * the order between the src and the destination is unspecified.
	 * 
	 * @param comm
	 * @param dLink
	 * @return
	 */
	protected Constraint makeLinkActivationConstraint(Task comm, DirectedLink dLink) {
		CLRR linkActivation = Variables.varCLRR(comm, dLink.getLink(), dLink.getSource(), dLink.getDest());
		CRR orderVariable = Variables.varCRR(comm, dLink.getSource(), dLink.getDest());
		return Constraints.generatePositiveImplication(linkActivation, orderVariable);
	}

}
