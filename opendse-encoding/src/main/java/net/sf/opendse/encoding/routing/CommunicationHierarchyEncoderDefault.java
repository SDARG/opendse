package net.sf.opendse.encoding.routing;

import java.util.HashSet;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;
import net.sf.opendse.encoding.constraints.Constraints;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.DDLRR;
import net.sf.opendse.encoding.variables.DDR;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class CommunicationHierarchyEncoderDefault implements CommunicationHierarchyEncoder {

	@Override
	public Set<Constraint> toConstraints(T communicationVariable, Set<CommunicationFlow> communicationFlows,
			Architecture<Resource, Link> routing) {
		Set<Constraint> result = new HashSet<Constraint>();
		Task communication = communicationVariable.getTask();
		// encodes the hierarchy on link level
		for (DirectedLink dLink : Models.getLinks(routing)) {
			CLRR communicationLinkVariable = Variables.varCLRR(communication, dLink.getLink(), dLink.getSource(),
					dLink.getDest());
			Set<DDLRR> commFlowLinkVariables = new HashSet<DDLRR>();
			for (CommunicationFlow flow : communicationFlows) {
				commFlowLinkVariables.add(Variables.varDDLRR(flow, dLink));
			}
			result.addAll(Constraints.generateOrConstraints(commFlowLinkVariables, communicationLinkVariable));
		}
		// encodes the hierarchy on resource level
		for (Resource res : routing) {
			CR communicationResourceVariable = Variables.varCR(communication, res);
			Set<DDR> commFlowResourceVariables = new HashSet<DDR>();
			for (CommunicationFlow flow : communicationFlows) {
				commFlowResourceVariables.add(Variables.varDDR(flow, res));
			}
			result.addAll(Constraints.generateOrConstraints(commFlowResourceVariables, communicationResourceVariable));
		}
		return result;
	}
}