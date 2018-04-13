package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Models.DirectedLink;

public class ActivationEncoderDefault implements ActivationEncoder {

	@Override
	public Set<Constraint> toConstraints(CommunicationFlow communicationFlow, Architecture<Resource, Link> routing) {
		Set<Constraint> activationConstraints = new HashSet<Constraint>();

		for (DirectedLink directedLink : Models.getLinks(routing)) {
			activationConstraints.add(Constraints.generateNegativeImplication(communicationFlow.getSourceDTT(),
					Variables.varDDLRR(communicationFlow, directedLink)));
			activationConstraints.add(Constraints.generateNegativeImplication(communicationFlow.getDestinationDTT(),
					Variables.varDDLRR(communicationFlow, directedLink)));
		}

		for (Resource res : routing) {
			activationConstraints.add(Constraints.generateNegativeImplication(communicationFlow.getSourceDTT(),
					Variables.varDDR(communicationFlow, res)));
			activationConstraints.add(Constraints.generateNegativeImplication(communicationFlow.getDestinationDTT(),
					Variables.varDDR(communicationFlow, res)));
		}
		return activationConstraints;
	}
}
