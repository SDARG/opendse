package net.sf.opendse.encoding.interpreter;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import org.opt4j.core.Individual;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.AllocationEncoding;
import net.sf.opendse.encoding.ImplementationEncodingModular;
import net.sf.opendse.encoding.MappingEncoding;
import net.sf.opendse.encoding.RoutingEncoding;
import net.sf.opendse.encoding.variables.AllocationVariable;
import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.CR;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.InterfaceVariable;
import net.sf.opendse.encoding.variables.L;
import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.MappingVariable;
import net.sf.opendse.encoding.variables.R;
import net.sf.opendse.encoding.variables.RoutingVariable;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variable;
import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Routings;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.Task;

/**
 * The {@link InterpreterVariable} gets the set of {@link InterfaceVariable}s created by
 * the {@link ImplementationEncodingModular} together with a {@link Model} satisfying
 * the constraints and uses them to create the implementation.
 * {@link Specification}.
 * 
 * @author Fedor Smirnov
 *
 */
public class InterpreterVariable extends InterpreterAbstract{

	protected final ImplementationEncodingModular implementationEncoding;
	protected boolean variablesInitialized = false;
	protected Set<ApplicationVariable> applicationVariables = new HashSet<ApplicationVariable>();
	protected Set<MappingVariable> mappingVariables = new HashSet<MappingVariable>();
	protected Set<RoutingVariable> routingVariables = new HashSet<RoutingVariable>();
	protected Set<AllocationVariable> allocationVariables = new HashSet<AllocationVariable>();

	@Inject
	public InterpreterVariable(SpecificationPostProcessor postProcessor, ImplementationEncodingModular implementationEncoding) {
		super(postProcessor);
		this.implementationEncoding = implementationEncoding;
	}

	@Override
	public Specification decodeModel(Specification specification, Model model) {
		if (!variablesInitialized) {
			initializeInterfaceVariables();
		}
		Application<Task, Dependency> implementationApplication = decodeApplication(applicationVariables, model,
				specification.getApplication());
		Architecture<Resource, Link> implementationAllocation = decodeAllocation(allocationVariables, model,
				specification.getArchitecture());
		Mappings<Task, Resource> implementationMappings = decodeMappings(mappingVariables, model,
				specification.getMappings(), implementationAllocation, implementationApplication);
		Routings<Task, Resource, Link> implementationRoutings = decodeRoutings(routingVariables, model,
				implementationApplication, implementationAllocation);
		return new Specification(implementationApplication, implementationAllocation, implementationMappings,
				implementationRoutings);
	}

	/**
	 * Decodes and returns the implementation {@link Routings}.
	 * 
	 * @param routingVariables
	 *            the set of {@link RoutingVariable}s created by the
	 *            {@link RoutingEncoding}
	 * @param model
	 *            the {@link Model} containing the assignment of the
	 *            {@link Variable}s satisfying the {@link Constraint}s
	 * @param implementationApplication
	 *            the {@link Application} of the {@link Individual} that is being
	 *            decoded
	 * @param implementationAllocation
	 *            the {@link Architecture} of the {@link Individual} that is being
	 *            decoded
	 * @return the {@link Routings} of the {@link Individual} that is being decoded
	 */
	protected Routings<Task, Resource, Link> decodeRoutings(Set<RoutingVariable> routingVariables, Model model,
			Application<Task, Dependency> implementationApplication,
			Architecture<Resource, Link> implementationAllocation) {
		Routings<Task, Resource, Link> result = new Routings<Task, Resource, Link>();
		for (RoutingVariable routingVar : routingVariables) {
			if (routingVar instanceof CLRR) {
				CLRR clrrVar = (CLRR) routingVar;
				processCLRRvariable(clrrVar, model, implementationAllocation, implementationApplication, result);
			} else if (routingVar instanceof CR) {
				CR crVar = (CR) routingVar;
				processCRvariable(crVar, model, implementationAllocation, implementationApplication, result);
			} else {
				throw new IllegalArgumentException("Unknown type of routing variable: " + routingVar.getClass());
			}
		}
		return result;
	}

	/**
	 * 
	 * Checks whether the given {@link CLRR} variable is activated in the
	 * {@link Model}. Adds the corresponding {@link Link} to the routing graph of
	 * the corresponding {@link Communication} if the CR variable is activated.
	 * 
	 * @param clrrVar
	 *            the {@link CLRR} variable that is checked
	 * @param model
	 *            the {@link Model} containing the assignment of the
	 *            {@link Variable}s satisfying the {@link Constraint}s
	 * @param implementationAllocation
	 *            the {@link Architecture} of the {@link Individual} that is being
	 *            decoded
	 * @param implementationApplication
	 *            the {@link Application} of the {@link Individual} that is being
	 *            decoded
	 * @param implementationRoutings
	 *            the {@link Routings} of the {@link Individual} that is being
	 *            decoded
	 */
	protected void processCLRRvariable(CLRR clrrVar, Model model, Architecture<Resource, Link> implementationAllocation,
			Application<Task, Dependency> implementationApplication,
			Routings<Task, Resource, Link> implementationRoutings) {
		checkVariableSetting(model, clrrVar);
		if (model.get(clrrVar)) {
			Communication comm = (Communication) clrrVar.getCommunication();
			Resource src = clrrVar.getSource();
			Resource dest = clrrVar.getDestination();
			Link link = (Link) clrrVar.getLink();
			if (implementationAllocation.getVertex(dest.getId()) == null)
				missingElementException(dest);
			if (implementationAllocation.getVertex(src.getId()) == null)
				missingElementException(src);
			if (implementationAllocation.getEdge(link.getId()) == null)
				missingElementException(link);
			if (implementationApplication.getVertex(comm.getId()) == null)
				missingElementException(comm);
			Resource routingSrc = copy(implementationAllocation.getVertex(src));
			Resource routingDest = copy(implementationAllocation.getVertex(dest));
			Link routingLink = copy(implementationAllocation.getEdge(link));
			if (implementationRoutings.get(comm).getEdge(routingLink) != null) {
				throw new IllegalArgumentException("Link already in routing!");
			}
			implementationRoutings.get(comm).addEdge(routingLink, routingSrc, routingDest, EdgeType.DIRECTED);
		}
	}

	/**
	 * Checks whether the given {@link CR} variable is activated in the
	 * {@link Model}. Adds the corresponding {@link Resource} to the routing graph
	 * of the corresponding {@link Communication} if the CR variable is activated.
	 * 
	 * @param crVar
	 *            the {@link CR} variable that is checked
	 * @param model
	 *            the {@link Model} containing the assignment of the
	 *            {@link Variable}s satisfying the {@link Constraint}s
	 * @param implementationAllocation
	 *            the {@link Architecture} of the {@link Individual} that is being
	 *            decoded
	 * @param implementationApplication
	 *            the {@link Application} of the {@link Individual} that is being
	 *            decoded
	 * @param implementationRoutings
	 *            the {@link Routings} of the {@link Individual} that is being
	 *            decoded
	 */
	protected void processCRvariable(CR crVar, Model model, Architecture<Resource, Link> implementationAllocation,
			Application<Task, Dependency> implementationApplication,
			Routings<Task, Resource, Link> implementationRoutings) {
		checkVariableSetting(model, crVar);
		if (model.get(crVar)) {
			Communication comm = (Communication) crVar.getCommunication();
			Resource res = crVar.getResource();
			if (implementationAllocation.getVertex(res.getId()) == null)
				missingElementException(res);
			if (implementationApplication.getVertex(comm.getId()) == null)
				missingElementException(comm);
			Architecture<Resource, Link> routing = implementationRoutings.get(comm);
			if (!routing.containsVertex(res)) {
				Resource routingResource = copy(res);
				routing.addVertex(routingResource);
			}
		}
	}

	/**
	 * Throws the {@link IllegalArgumentException} that gives notice that the given
	 * {@link Element} is missing in the parts of the {@link Specification} created
	 * so far.
	 * 
	 * @param e
	 *            the potentially missing {@link Element}
	 */
	protected void missingElementException(Element e) {
		String message = "Element missing in the implementation: ";
		message += e.getId();
		throw new IllegalArgumentException(message);
	}

	/**
	 * Decodes and returns the implementation {@link Mappings}.
	 * 
	 * @param mappingVariables
	 *            the set of {@link MappingVariable}s encoded by the
	 *            {@link MappingEncoding}
	 * @param model
	 *            the {@link Model} containing a {@link Variable} assignment that
	 *            solves the {@link Constraint}s encoded by the
	 *            {@link ImplementationEncodingModular}
	 * @param specificationMappings
	 *            the {@link Mappings} provided by the user as part of the
	 *            {@link Specification} describing the overall problem
	 * @return the implementation {@link Mappings}
	 */
	protected Mappings<Task, Resource> decodeMappings(Set<MappingVariable> mappingVariables, Model model,
			Mappings<Task, Resource> specificationMappings, Architecture<Resource, Link> implementationArchitecture,
			Application<Task, Dependency> implementationApplication) {
		Mappings<Task, Resource> result = new Mappings<Task, Resource>();
		for (MappingVariable mappingVar : mappingVariables) {
			checkVariableSetting(model, mappingVar);
			if (model.get(mappingVar)) {
				if (mappingVar instanceof M) {
					M mVar = (M) mappingVar;
					Mapping<Task, Resource> encodedMapping = mVar.getMapping();
					Mapping<Task, Resource> implementationMapping = getImplementationMapping(encodedMapping,
							specificationMappings, implementationArchitecture, implementationApplication);
					result.add(implementationMapping);
				} else {
					throw new IllegalArgumentException("Unknown type of mapping variable " + mappingVar.getClass());
				}
			}
		}
		return result;
	}

	/**
	 * Makes a child {@link Mapping} if the mapping is present in the specification.
	 * Otherwise just returns the mapping.
	 * 
	 * @param encodedMapping
	 *            the {@link Mapping} encoded in the constraint {@link Variable}s
	 * @param specificationMappings
	 *            the {@link Mappings} provided by the user
	 * @return a child {@link Mapping} if the mapping is present in the
	 *         specification. Otherwise just returns the given mapping
	 */
	protected Mapping<Task, Resource> getImplementationMapping(Mapping<Task, Resource> encodedMapping,
			Mappings<Task, Resource> specificationMappings, Architecture<Resource, Link> implementationArchitecture,
			Application<Task, Dependency> implementationApplication) {
		Mapping<Task, Resource> implementationMapping = null;
		if (implementationArchitecture.getVertex(encodedMapping.getTarget()) == null) {
			throw new IllegalArgumentException("Mapping target not in the implementation");
		}
		if (implementationApplication.getVertex(encodedMapping.getSource()) == null) {
			throw new IllegalArgumentException("Mapping source not in the implementation");
		}
		Resource target = implementationArchitecture.getVertex(encodedMapping.getTarget());
		Task source = implementationApplication.getVertex(encodedMapping.getSource());
		if (specificationMappings.get(encodedMapping.getSource(), encodedMapping.getTarget()).size() == 1) {
			Mapping<Task, Resource> specificationMapping = specificationMappings
					.get(encodedMapping.getSource(), encodedMapping.getTarget()).iterator().next();
			implementationMapping = new Mapping<Task, Resource>(specificationMapping, source, target);
		} else {
			implementationMapping = encodedMapping;
			implementationMapping.setTarget(implementationArchitecture.getVertex(encodedMapping.getTarget()));
			implementationMapping.setSource(implementationApplication.getVertex(encodedMapping.getSource()));
		}
		return implementationMapping;
	}

	/**
	 * Decodes and returns the {@link Architecture} for the {@link Individual} that
	 * is being decoded.
	 * 
	 * @param allocationVariables
	 *            the set of {@link AllocationVariable}s created by the
	 *            {@link AllocationEncoding}
	 * @param model
	 *            the {@link Model} containing a {@link Variable} assignment that
	 *            solves the {@link Constraint}s encoded by the
	 *            {@link ImplementationEncodingModular}
	 * @param specificationArchitecture
	 *            the {@link Architecture} from the {@link Specification} describing
	 *            the overall problem
	 * @return the {@link Architecture} for the {@link Individual} that is being
	 *         decoded
	 */
	protected Architecture<Resource, Link> decodeAllocation(Set<AllocationVariable> allocationVariables, Model model,
			Architecture<Resource, Link> specificationArchitecture) {
		Architecture<Resource, Link> result = new Architecture<Resource, Link>();
		for (AllocationVariable alloVar : allocationVariables) {
			checkVariableSetting(model, alloVar);
			if (model.get(alloVar)) {
				if (alloVar instanceof R) {
					R rVar = (R) alloVar;
					Resource encodedRes = rVar.getResource();
					Resource implRes = getImplementationResource(encodedRes, specificationArchitecture);
					if (result.getVertex(implRes) == null) {
						result.addVertex(implRes);
					}
				} else if (alloVar instanceof L) {
					L lVar = (L) alloVar;
					Link encodedLink = lVar.getLink();
					addImplementationLink(encodedLink, result, specificationArchitecture);
				} else {
					throw new IllegalArgumentException("Unknown type of allocation variable " + alloVar.getClass());
				}
			}
		}
		return result;
	}

	/**
	 * Creates the implementation {@link Link} (and its end points, if necessary)
	 * and adds it to the implementation {@link Architecture}.
	 * 
	 * @param encodedLink
	 *            the {@link Link} object used for the creation of the link-related
	 *            constraint {@link Variable}s
	 * @param implementationAllocation
	 *            the {@link Architecture} of the {@link Individual} that is being
	 *            decoded
	 * @param specificationArchitecture
	 *            the {@link Architecture} from the {@link Specification} describing
	 *            the overall problem
	 */
	protected void addImplementationLink(Link encodedLink, Architecture<Resource, Link> implementationAllocation,
			Architecture<Resource, Link> specificationArchitecture) {
		Link implementationLink = getImplementationLink(encodedLink, specificationArchitecture);
		Resource implementationEndpoint1 = getImplementationResource(
				specificationArchitecture.getEndpoints(encodedLink).getFirst(), specificationArchitecture);
		Resource implementationEndpoint2 = getImplementationResource(
				specificationArchitecture.getEndpoints(encodedLink).getSecond(), specificationArchitecture);
		if (implementationAllocation.getEdge(implementationLink) == null) {
			implementationAllocation.addEdge(implementationLink, implementationEndpoint1, implementationEndpoint2,
					EdgeType.UNDIRECTED);
		}
	}

	/**
	 * Creates and returns a {@link Link} that is the child of the {@link Link} in
	 * the given {@link Architecture}.
	 * 
	 * @param encodedLink
	 *            the {@link Link} to copy
	 * @param specificationArchitecture
	 *            the {@link Architecture} containing the link to copy
	 * @return a {@link Link} that is the child of the {@link Link} in the given
	 *         {@link Architecture}
	 */
	protected Link getImplementationLink(Link encodedLink, Architecture<Resource, Link> specificationArchitecture) {
		if (specificationArchitecture.getEdge(encodedLink) == null) {
			throw new IllegalArgumentException(
					"The encoded link " + encodedLink.getId() + " is not present in the specification architecture");
		}
		return copy(specificationArchitecture.getEdge(encodedLink));
	}

	/**
	 * Makes and returns a child {@link Resource} of the resource in the given
	 * {@link Architecture}.
	 * 
	 * @param encodedResource
	 *            the {@link Resource} to copy
	 * @param specificationArchitecture
	 *            the {@link Architecture} containing the resource to copy
	 * @return a child {@link Resource} of the resource in the given
	 *         {@link Architecture}
	 */
	protected Resource getImplementationResource(Resource encodedResource,
			Architecture<Resource, Link> specificationArchitecture) {
		if (specificationArchitecture.getVertex(encodedResource) == null) {
			throw new IllegalArgumentException("The encoded resource " + encodedResource.getId()
					+ " is not present in the specification architecture");
		}
		return copy(specificationArchitecture.getVertex(encodedResource));
	}

	/**
	 * Decodes and creates the implementation {@link Application} based on the set
	 * of {@link ApplicationVariable}s, the {@link Model} and the specification
	 * {@link Application}.
	 * 
	 * @param applicationVariables
	 *            the set of {@link ApplicationVariable}s used to encode a valid
	 *            {@link Application}
	 * @param model
	 *            the {@link Model} containing a {@link Variable} assignment
	 *            satisfying the constraints
	 * @param specificationApplication
	 *            the {@link Application} from the {@link Specification}
	 * @return the implementation {@link Application}
	 */
	protected Application<Task, Dependency> decodeApplication(Set<ApplicationVariable> applicationVariables,
			Model model, Application<Task, Dependency> specificationApplication) {
		Application<Task, Dependency> result = new Application<Task, Dependency>();
		Set<T> taskVariables = new HashSet<T>();
		Set<DTT> dependecyVariables = new HashSet<DTT>();
		for (ApplicationVariable var : applicationVariables) {
			if (var instanceof T) {
				taskVariables.add((T) var);
			} else if (var instanceof DTT) {
				dependecyVariables.add((DTT) var);
			} else {
				throw new IllegalArgumentException("Unknown Type of application variable: " + var.getClass());
			}
		}
		for (T tVar : taskVariables) {
			checkVariableSetting(model, tVar);
			if (model.get(tVar)) {
				Task task = tVar.getTask();
				Task implementationTask = createImplementationTask(task, specificationApplication);
				result.addVertex(implementationTask);
			}
		}
		for (DTT dttVar : dependecyVariables) {
			checkVariableSetting(model, dttVar);
			if (model.get(dttVar)) {
				Task srcTask = result.getVertex(dttVar.getSourceTask().getId());
				Task destTask = result.getVertex(dttVar.getDestinationTask().getId());
				if (srcTask == null || destTask == null) {
					throw new IllegalArgumentException("An end point of an activated dependency is not activated");
				}
				Dependency dependency = createImplementationDependency(dttVar.getDependency(),
						specificationApplication);
				result.addEdge(dependency, srcTask, destTask, EdgeType.DIRECTED);
			}
		}
		return result;
	}

	/**
	 * Throws an {@link IllegalArgumentException} if the given
	 * {@link InterfaceVariable} is not set in the given {@link Model}.
	 * 
	 * @param model
	 *            the {@link Model} containing the {@link Variable} assignment
	 * @param var
	 *            the {@link InterfaceVariable} that is to be checked
	 */
	protected void checkVariableSetting(Model model, InterfaceVariable var) {
		if (model.get(var) == null) {
			throw new IllegalArgumentException(getNotEncodedMessage(var));
		}
	}

	/**
	 * Makes a child {@link Dependency} if the encoded dependency is in the original
	 * {@link Application}. Returns the given dependency otherwise.
	 * 
	 * @param encodedDependency
	 *            the {@link Dependency} encoded in a dependency-related
	 *            {@link Variable}
	 * @param specificationApplication
	 *            the {@link Application} from the {@link Specification} describing
	 *            the overall problem
	 * @return a child {@link Dependency} if the encoded dependency is in the
	 *         original {@link Application}. Returns the given dependency otherwise
	 */
	protected Dependency createImplementationDependency(Dependency encodedDependency,
			Application<Task, Dependency> specificationApplication) {
		if (specificationApplication.getEdge(encodedDependency) != null) {
			return copy(specificationApplication.getEdge(encodedDependency));
		} else {
			return encodedDependency;
		}
	}

	/**
	 * Returns a child {@link Task} if the encoded task is in the original
	 * {@link Application}. Returns the given task otherwise.
	 * 
	 * @param encodedTask
	 *            the {@link Task} encoded in a task-related {@link Variable}
	 * @param specificationApplication
	 *            the {@link Application} from the {@link Specification} describing
	 *            the overall problem
	 * @return a child {@link Task} if the encoded task is in the original
	 *         {@link Application}. Returns the given task otherwise
	 */
	protected Task createImplementationTask(Task encodedTask, Application<Task, Dependency> specificationApplication) {
		if (specificationApplication.getVertex(encodedTask) != null) {
			return copy(specificationApplication.getVertex(encodedTask));
		} else {
			return encodedTask;
		}
	}

	/**
	 * Reads the {@link InterfaceVariable}s from the {@link ImplementationEncodingModular}.
	 */
	protected void initializeInterfaceVariables() {
		for (InterfaceVariable interfaceVariable : implementationEncoding.getInterfaceVariables()) {
			if (interfaceVariable instanceof ApplicationVariable) {
				applicationVariables.add((ApplicationVariable) interfaceVariable);
			} else if (interfaceVariable instanceof MappingVariable) {
				mappingVariables.add((MappingVariable) interfaceVariable);
			} else if (interfaceVariable instanceof RoutingVariable) {
				routingVariables.add((RoutingVariable) interfaceVariable);
			} else if (interfaceVariable instanceof AllocationVariable) {
				allocationVariables.add((AllocationVariable) interfaceVariable);
			} else {
				throw new IllegalArgumentException(
						"The variable " + interfaceVariable + " is an instance of an unknown class.");
			}
		}
		variablesInitialized = true;
	}

	/**
	 * Returns a copy of the given {@link Element}.
	 * 
	 * @param element
	 *            the {@link Element} to be copied
	 * @return a copy of the given {@link Element}
	 */
	@SuppressWarnings("unchecked")
	public <E extends Element> E copy(Element element) {
		try {
			Constructor<? extends Element> cstr = element.getClass().getConstructor(Element.class);
			Element copy = cstr.newInstance(element);
			return (E) copy;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the error message that is to be displayed if an
	 * {@link InterfaceVariable} is not contained in the {@link Model}.
	 * 
	 * @param var
	 *            the {@link InterfaceVariable} in question
	 * @return the error message that is to be displayed if an
	 *         {@link InterfaceVariable} is not contained in the {@link Model}
	 */
	protected String getNotEncodedMessage(InterfaceVariable var) {
		return "The variable " + var.toString() + " is not encoded";
	}
}
