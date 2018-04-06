package net.sf.opendse.encoding.variables;

import java.util.HashMap;
import java.util.Map;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;

import net.sf.opendse.encoding.routing.CommunicationFlow;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * {@link Variables} offers contains static methods for the creation and
 * maintenance of the {@link Variable}s used for the encoding of the
 * {@link Constraint}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class Variables {

	protected static final Map<Variable, Literal> pCache = new HashMap<Variable, Literal>();
	protected static final Map<Variable, Literal> nCache = new HashMap<Variable, Literal>();

	private Variables() {
	}

	public static L varL(Link link) {
		return new L(link);
	}
	
	public static R varR(Resource resource) {
		return new R(resource);
	}
	
	public static DDdR varDDdR(CommunicationFlow communicationFlow, Resource resource) {
		return new DDdR(communicationFlow, resource);
	}

	public static DDsR varDDsR(CommunicationFlow communicationFlow, Resource resource) {
		return new DDsR(communicationFlow, resource);
	}

	public static DDLRR varDDLRR(CommunicationFlow communicationFlow, DirectedLink directedLink) {
		return varDDLRR(communicationFlow, directedLink.getLink(), directedLink.getSource(), directedLink.getDest());
	}

	public static DDLRR varDDLRR(CommunicationFlow communicationFlow, Link link, Resource sourceResource,
			Resource destResource) {
		return new DDLRR(communicationFlow, link, sourceResource, destResource);
	}

	public static DDR varDDR(CommunicationFlow communicationFlow, Resource resource) {
		return new DDR(communicationFlow, resource);
	}

	public static CLRR varCLRR(Task communication, Link link, Resource source, Resource destination) {
		return new CLRR(communication, link, source, destination);
	}

	public static CR varCR(Task communication, Resource resource) {
		return new CR(communication, resource);
	}

	public static M varM(Mapping<Task, Resource> mapping) {
		return new M(mapping);
	}

	public static DTT varDTT(Dependency dependency, Task sourceTask, Task destinationTask) {
		return new DTT(dependency, sourceTask, destinationTask);
	}

	public static T varT(Task task) {
		return new T(task);
	}

	/**
	 * returns the positive literal for the given variable
	 * 
	 * @param variable
	 * @return the positive literal
	 */
	public static Literal p(Variable variable) {
		if (!pCache.containsKey(variable)) {
			pCache.put(variable, new Literal(variable, true));
		}
		return pCache.get(variable);
	}

	/**
	 * returns the negative literal for the given variable
	 * 
	 * @param variable
	 * @return the negative literal for the given variable
	 */
	public static Literal n(Variable variable) {
		if (!nCache.containsKey(variable)) {
			nCache.put(variable, new Literal(variable, false));
		}
		return nCache.get(variable);
	}
}
