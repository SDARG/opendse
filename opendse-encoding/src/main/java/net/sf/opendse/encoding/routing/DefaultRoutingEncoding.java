package net.sf.opendse.encoding.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.TaskPropertyService;

public class DefaultRoutingEncoding implements RoutingEncoding {

	@Override
	public Set<RoutingVariable> toConstraints(Set<ApplicationVariable> applicationVariables,
			Set<MappingVariable> mappingVariables, Routings<Task, Resource, Link> routings,
			Set<Constraint> constraints) {

		Map<T, Set<CommunicationFlow>> communicationFlowMap = findCommunicationFlows(applicationVariables);

		return null;
	}

	/**
	 * Finds all communication flows encoded by the application variables.
	 * 
	 * @param applicationVariables
	 * @return a map where the variables encoding the communication activations are
	 *         mapped onto the set of their communication flows
	 */
	protected Map<T, Set<CommunicationFlow>> findCommunicationFlows(Set<ApplicationVariable> applicationVariables) {
		Map<T, Set<DTT>> sourceMap = new HashMap<T, Set<DTT>>();
		Map<T, Set<DTT>> destMap = new HashMap<T, Set<DTT>>();
		for (ApplicationVariable applVar : applicationVariables) {
			if (applVar instanceof DTT) {
				DTT dttVar = (DTT) applVar;
				Task communication = TaskPropertyService.isCommunication(dttVar.getSourceTask()) ? dttVar.getSourceTask() : dttVar.getDestinationTask();
				T commVar = Variables.var(communication);
				Map<T, Set<DTT>> targetMap = TaskPropertyService.isCommunication(dttVar.getSourceTask()) ? destMap : sourceMap;
				if (!targetMap.containsKey(commVar)) {
					targetMap.put(commVar, new HashSet<DTT>());
				}
				targetMap.get(commVar).add(dttVar);
			}
		}
		Set<T> commVars = new HashSet<T>(sourceMap.keySet());
		commVars.addAll(destMap.keySet());
		Map<T, Set<CommunicationFlow>> result = new HashMap<T, Set<CommunicationFlow>>();
		for (T commVar : commVars) {
			result.put(commVar, new HashSet<CommunicationFlow>());
			Set<DTT> sourceDTTs = sourceMap.get(commVar);
			Set<DTT> destDTTs = destMap.get(commVar);
			for (DTT srcDTT : sourceDTTs) {
				for (DTT destDTT : destDTTs) {
					result.get(commVar).add(new CommunicationFlow(srcDTT, destDTT));
				}
			}
		}
		return result;
	}
}
