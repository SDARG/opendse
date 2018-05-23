/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * The {@code Graphs} provides several useful static methods and classes.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Models {

	/**
	 * The {@code IsCommunicationPredicate} is a {@code Predicate} that returns true
	 * if the task implements the {@link ICommunication} interface.
	 * 
	 * @author Martin Lukasiewycz
	 * 
	 */
	protected static class IsCommunicationPredicate implements Predicate<Task> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.commons.collections15.Predicate#evaluate(java.lang.Object)
		 */
		@Override
		public boolean evaluate(Task task) {
			return task instanceof ICommunication;
		}
	}

	/**
	 * The instance of the {@code IsCommunicationPredicate} class.
	 */
	protected static IsCommunicationPredicate isCommunicationPredicate = new IsCommunicationPredicate();

	/**
	 * The {@code OnlyCommunicationIterator} is an iterator for tasks that only
	 * considers communication tasks.
	 * 
	 * @author Martin Lukasiewycz
	 * 
	 */
	protected static class OnlyCommunicationIterator extends FilterIterator<Task> {

		/**
		 * Constructs an {@code OnlyCommunicationIterator}.
		 * 
		 * @param iterator
		 *            the parent iterator
		 */
		public OnlyCommunicationIterator(Iterator<Task> iterator) {
			super(iterator, new IsCommunicationPredicate());
		}
	}

	/**
	 * The instance of the {@code IsProcessPredicate} class.
	 */
	protected static IsProcessPredicate isProcessPredicate = new IsProcessPredicate();

	/**
	 * The {@code IsCommunicationPredicate} is a {@code Predicate} that returns true
	 * if the task does not implement the {@link ICommunication} interface.
	 * 
	 * @author Martin Lukasiewycz
	 * 
	 */
	protected static class IsProcessPredicate implements Predicate<Task> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.commons.collections15.Predicate#evaluate(java.lang.Object)
		 */
		@Override
		public boolean evaluate(Task task) {
			return !(task instanceof ICommunication);
		}
	}

	/**
	 * The {@code OnlyCommunicationIterator} is an iterator for tasks that only
	 * considers process tasks.
	 * 
	 * @author Martin Lukasiewycz
	 * 
	 */
	protected static class OnlyProcessIterator extends FilterIterator<Task> {

		/**
		 * Constructs an {@code OnlyProcessIterator}.
		 * 
		 * @param iterator
		 *            the parent iterator
		 */
		public OnlyProcessIterator(Iterator<Task> iterator) {
			super(iterator, new IsProcessPredicate());
		}
	}

	/**
	 * Filters an {@code Iterable} such that only communication tasks are
	 * considered.
	 * 
	 * @param iterable
	 *            the iterable
	 * @return the filtered iterable
	 */
	public static Iterable<Task> filterCommunications(final Iterable<Task> iterable) {
		return new Iterable<Task>() {
			@Override
			public Iterator<Task> iterator() {
				return new OnlyCommunicationIterator(iterable.iterator());
			}
		};
	}

	/**
	 * Filters an {@code Iterable} such that only process tasks are considered.
	 * 
	 * @param iterable
	 *            the iterable
	 * @return the filtered iterable
	 */
	public static Iterable<Task> filterProcesses(final Iterable<Task> iterable) {
		return new Iterable<Task>() {
			@Override
			public Iterator<Task> iterator() {
				return new OnlyProcessIterator(iterable.iterator());
			}
		};
	}

	/**
	 * Returns {@code true} if the task is a process.
	 * 
	 * @param task
	 *            the task
	 * @return {@code true} if the task is a process
	 */
	public static boolean isProcess(Task task) {
		return isProcessPredicate.evaluate(task);
	}

	/**
	 * Returns {@code true} if the task is a communication.
	 * 
	 * @param task
	 *            the task
	 * @return {@code true} if the task is a communication
	 */
	public static boolean isCommunication(Task task) {
		return isCommunicationPredicate.evaluate(task);
	}

	/**
	 * The {@code DirectedLink} is a {@link Link} wrapper that contains the source
	 * {@code Resource} and destination {@code Resource}.
	 * 
	 * @author Martin Lukasiewycz
	 * 
	 */
	public static class DirectedLink {
		final Link l;
		final Resource r0;
		final Resource r1;

		/**
		 * Constructs a {@code DirectedLink}.
		 * 
		 * @param l
		 *            the link
		 * @param r0
		 *            the source
		 * @param r1
		 *            the destination
		 */
		public DirectedLink(Link l, Resource r0, Resource r1) {
			super();
			this.l = l;
			this.r0 = r0;
			this.r1 = r1;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DirectedLink) {
				DirectedLink other = (DirectedLink) obj;
				return (this.l.equals(other.l) && this.r0.equals(other.r0) && this.r1.equals(other.r1));
			}
			return false;
		}

		/**
		 * Returns the wrapped {@code Link}.
		 * 
		 * @return the link
		 */
		public Link getLink() {
			return l;
		}

		/**
		 * Returns the source {@code Resource}.
		 * 
		 * @return the source resource
		 */
		public Resource getSource() {
			return r0;
		}

		/**
		 * Returns the destination {@code Resource}.
		 * 
		 * @return the destination resource
		 */
		public Resource getDest() {
			return r1;
		}
	}

	/**
	 * Returns the list of {@code DirectedLink} elements that have a given source
	 * {code Resource}.
	 * 
	 * @param architecture
	 *            the architecture
	 * @param r0
	 *            the source
	 * @return all outgoing directed links
	 */
	public static List<DirectedLink> getOutLinks(Architecture<Resource, Link> architecture, Resource r0) {
		assert (architecture != null);
		assert (r0 != null);
		assert (architecture.containsVertex(r0));

		List<DirectedLink> list = new ArrayList<DirectedLink>();
		for (Link link : architecture.getOutEdges(r0)) {
			Resource r1 = architecture.getOpposite(r0, link);
			list.add(new DirectedLink(link, r0, r1));
		}
		return list;
	}

	/**
	 * Returns the list of {@code DirectedLink} elements that have a given
	 * destination {code Resource}.
	 * 
	 * @param architecture
	 *            the architecture
	 * @param r0
	 *            the source
	 * @return all incoming directed links
	 */
	public static List<DirectedLink> getInLinks(Architecture<Resource, Link> architecture, Resource r0) {
		List<DirectedLink> list = new ArrayList<DirectedLink>();
		for (Link link : architecture.getInEdges(r0)) {
			Resource r1 = architecture.getOpposite(r0, link);
			list.add(new DirectedLink(link, r1, r0));
		}
		return list;
	}

	/**
	 * Returns all {@code DirectedLink} elements of an {@code Architecture}.
	 * 
	 * @param architecture
	 *            the architecture
	 * @return all directed links
	 */
	public static List<DirectedLink> getLinks(Architecture<Resource, Link> architecture) {
		List<DirectedLink> list = new ArrayList<DirectedLink>();
		for (Resource r0 : architecture.getVertices()) {
			list.addAll(getOutLinks(architecture, r0));
		}
		return list;
	}

	/**
	 * Returns all {@code DirectedLink} elements of an {@code Architecture} for
	 * given {@code Link}.
	 * 
	 * @param architecture
	 *            the architecture
	 * @param link
	 *            the link
	 * @return all directed links for a given link
	 */
	public static List<DirectedLink> getLinks(Architecture<Resource, Link> architecture, Link link) {
		List<DirectedLink> list = new ArrayList<DirectedLink>();
		Pair<Resource> pair = architecture.getEndpoints(link);
		Resource r0 = pair.getFirst();
		Resource r1 = pair.getSecond();

		if (architecture.getEdgeType(link) == EdgeType.UNDIRECTED) {
			list.add(new DirectedLink(link, r0, r1));
			list.add(new DirectedLink(link, r1, r0));
		} else {
			list.add(new DirectedLink(link, r0, r1));
		}

		return list;
	}

	/**
	 * Returns a map of all pairs of {@code ids} and the corresponding
	 * {@code Element} objects.
	 * 
	 * @param specification
	 *            the specification
	 * @return the map
	 */
	public static Map<String, Element> getElementsMap(Specification specification) {
		Map<String, Element> elements = new HashMap<String, Element>();

		Application<Task, Dependency> application = specification.getApplication();
		Architecture<Resource, Link> architecture = specification.getArchitecture();
		Mappings<Task, Resource> mappings = specification.getMappings();

		for (Resource resource : architecture) {
			elements.put(resource.getId(), resource);
		}
		for (Link link : architecture.getEdges()) {
			elements.put(link.getId(), link);
		}
		for (Task task : application) {
			elements.put(task.getId(), task);
		}
		for (Dependency dependency : application.getEdges()) {
			elements.put(dependency.getId(), dependency);
		}
		for (Mapping<Task, Resource> mapping : mappings) {
			elements.put(mapping.getId(), mapping);
		}
		return elements;
	}

	/**
	 * Returns all {@code Element} objects of a {@code Specification}.
	 * 
	 * @param specification
	 *            the specification
	 * @return all element objects
	 */
	public static Set<Element> getElements(Specification specification) {
		Application<Task, Dependency> application = specification.getApplication();
		Architecture<Resource, Link> architecture = specification.getArchitecture();
		Mappings<Task, Resource> mappings = specification.getMappings();

		Set<Element> elements = new HashSet<Element>();
		elements.addAll(application.getVertices());
		elements.addAll(application.getEdges());
		elements.addAll(architecture.getVertices());
		elements.addAll(architecture.getEdges());
		elements.addAll(mappings.getAll());
		return elements;
	}

	/**
	 * Returns an {@link Iterable} which returns only those {@link Element}s from
	 * the given {@code iterable} which are of one of the given {@code types}.
	 * 
	 * @param <E>
	 *            the type of the element
	 * @param iterable
	 *            the iterable over the source elements
	 * @param types
	 *            the types to filter for
	 * @return the iterable over the elements which are of one of the types
	 * @see Element#getType()
	 */
	public static <E extends Element> Iterable<E> filterType(final Iterable<E> iterable, final String... types) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new FilterIterator<E>(iterable.iterator(), new Predicate<E>() {
					@Override
					public boolean evaluate(E e) {
						for (String type : types) {
							if (type.equals(e.getType())) {
								return true;
							}
						}
						return false;
					}
				});
			}
		};
	}

	/**
	 * Creates a copy of the given {@link Specification} using
	 * {@link #copy(Element)}, i.e., create new instances for all {@link Element}s.
	 * 
	 * @param specification
	 * @return
	 */
	public static Specification copy(Specification specification) {
		Architecture<Resource, Link> sArchitecture = specification.getArchitecture();
		Application<Task, Dependency> sApplication = specification.getApplication();
		Mappings<Task, Resource> sMappings = specification.getMappings();
		Routings<Task, Resource, Link> sRoutings = specification.getRoutings();

		Architecture<Resource, Link> iArchitecture = copy(sArchitecture);
		Application<Task, Dependency> iApplication = copy(sApplication);
		Mappings<Task, Resource> iMappings = copyMappings(sMappings, iArchitecture, iApplication);
		Routings<Task, Resource, Link> iRoutings = copyRoutings(sApplication, sRoutings, iApplication, iArchitecture);

		return new Specification(iApplication, iArchitecture, iMappings, iRoutings);
	}

	public static Routings<Task, Resource, Link> copyRoutings(Application<Task, Dependency> sApplication,
			Routings<Task, Resource, Link> sRoutings, Application<Task, Dependency> iApplication,
			Architecture<Resource, Link> iArchitecture) {
		Routings<Task, Resource, Link> iRoutings = new Routings<Task, Resource, Link>();

		for (Task c : filterCommunications(sApplication)) {
			Architecture<Resource, Link> sRouting = sRoutings.get(c);
			Architecture<Resource, Link> iRouting = new Architecture<Resource, Link>();

			for (Resource r : sRouting) {
				r = iArchitecture.getVertex(r);
				iRouting.addVertex((Resource) copy(r));
			}
			for (Link l : sRouting.getEdges()) {
				Pair<Resource> endpoints = sRouting.getEndpoints(l);
				Resource r0 = iRouting.getVertex(endpoints.getFirst());
				Resource r1 = iRouting.getVertex(endpoints.getSecond());
				iRouting.addEdge((Link) copy(l), r0, r1, sRouting.getEdgeType(l));
			}

			iRoutings.set(iApplication.getVertex(c), iRouting);
		}
		return iRoutings;
	}

	public static Mappings<Task, Resource> copyMappings(Mappings<Task, Resource> sMappings,
			Architecture<Resource, Link> iArchitecture, Application<Task, Dependency> iApplication) {
		Mappings<Task, Resource> iMappings = new Mappings<Task, Resource>();

		for (Mapping<Task, Resource> m : sMappings) {
			Mapping<Task, Resource> copy = copy(m);
			copy.setSource(iApplication.getVertex(m.getSource()));
			copy.setTarget(iArchitecture.getVertex(m.getTarget()));
			iMappings.add(copy);
		}
		return iMappings;
	}

	public static Application<Task, Dependency> copy(Application<Task, Dependency> sApplication) {
		Application<Task, Dependency> iApplication = new Application<Task, Dependency>();

		// copy application (including function attributes)
		for (Task t : sApplication) {
			iApplication.addVertex((Task) copy(t));
		}
		for (Dependency e : sApplication.getEdges()) {
			Pair<Task> endpoints = sApplication.getEndpoints(e);
			Task source = iApplication.getVertex(endpoints.getFirst());
			Task dest = iApplication.getVertex(sApplication.getVertex(endpoints.getSecond()));
			iApplication.addEdge((Dependency) copy(e), source, dest, sApplication.getEdgeType(e));
		}

		for (Function<Task, Dependency> function : iApplication.getFunctions()) {
			Task t = function.iterator().next();
			setAttributes(function, sApplication.getFunction(t).getAttributes());
		}
		return iApplication;
	}

	public static Architecture<Resource, Link> copy(Architecture<Resource, Link> sArchitecture) {
		Architecture<Resource, Link> iArchitecture = new Architecture<Resource, Link>();
		for (Resource r : sArchitecture) {
			iArchitecture.addVertex((Resource) copy(r));
		}
		for (Link l : sArchitecture.getEdges()) {
			Pair<Resource> endpoints = sArchitecture.getEndpoints(l);
			Resource source = iArchitecture.getVertex(endpoints.getFirst());
			Resource dest = iArchitecture.getVertex(endpoints.getSecond());
			iArchitecture.addEdge((Link) copy(l), source, dest, sArchitecture.getEdgeType(l));
		}
		return iArchitecture;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Element> E copy(Element element) {
		try {
			Constructor<? extends Element> cstr = element.getClass().getConstructor(Element.class);
			Element copy = cstr.newInstance(element);
			return (E) copy;
		} catch (Exception e) {
			throw new RuntimeException("could not copy element " + element, e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <M extends Mapping<?, ?>> M copy(Mapping<?, ?> mapping) {
		try {
			Constructor<? extends Element> cstr = mapping.getClass().getConstructor(Element.class, Task.class,
					Resource.class);
			Element copy = cstr.newInstance(mapping, mapping.getSource(), mapping.getTarget());
			return (M) copy;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void setAttributes(IAttributes e, Attributes attributes) {
		for (String name : attributes.keySet()) {
			e.setAttribute(name, attributes.get(name));
		}
	}

	/**
	 * Creates a clone of the {@link Specification}, i.e., with identical
	 * {@link Element}s ({@code ==}).
	 * 
	 * @param specification
	 *            the specification to be cloned
	 * @return a clone of that specification
	 */
	public static Specification clone(Specification specification) {
		Application<Task, Dependency> iApplication = clone(specification.getApplication());
		Architecture<Resource, Link> iArchitecture = clone(specification.getArchitecture());
		Mappings<Task, Resource> iMappings = new Mappings<Task, Resource>();
		Routings<Task, Resource, Link> iRoutings = new Routings<Task, Resource, Link>();

		for (Mapping<Task, Resource> m : specification.getMappings()) {
			iMappings.add(m);
		}

		Routings<Task, Resource, Link> sRoutings = specification.getRoutings();
		for (Task c : filterCommunications(specification.getApplication())) {
			Architecture<Resource, Link> sRouting = sRoutings.get(c);
			iRoutings.set(c, sRouting);
		}

		Specification clone = new Specification(iApplication, iArchitecture, iMappings, iRoutings);
		return clone;
	}

	/**
	 * Creates a clone of the {@link Application}, i.e., with identical
	 * {@link Element}s ({@code ==}).
	 * 
	 * @param sApplication
	 *            the application to be cloned
	 * @return a clone of that application
	 */
	public static <T extends Task, D extends Dependency> Application<T, D> clone(Application<T, D> sApplication) {
		Application<T, D> iApplication = new Application<T, D>();
		// copy application (including function attributes)
		for (T t : sApplication) {
			iApplication.addVertex(t);
		}
		for (D e : sApplication.getEdges()) {
			iApplication.addEdge(e, sApplication.getEndpoints(e), sApplication.getEdgeType(e));
		}

		for (Function<T, D> function : iApplication.getFunctions()) {
			T t = function.iterator().next();
			setAttributes(function, sApplication.getFunction(t).getAttributes());
		}
		return iApplication;
	}

	/**
	 * Creates a clone of the {@link Architecture}, i.e., with identical
	 * {@link Element}s ({@code ==}).
	 * 
	 * @param sArchitecture
	 *            the architecture to be cloned
	 * @return a clone of that architecture
	 */
	public static <R extends Resource, L extends Link> Architecture<R, L> clone(Architecture<R, L> sArchitecture) {
		Architecture<R, L> iArchitecture = new Architecture<R, L>();
		for (R r : sArchitecture) {
			iArchitecture.addVertex(r);
		}
		for (L l : sArchitecture.getEdges()) {
			iArchitecture.addEdge(l, sArchitecture.getEndpoints(l), sArchitecture.getEdgeType(l));
		}
		return iArchitecture;
	}

	public static void filterByResources(Specification specification, Collection<Resource> resources) {

		Set<Resource> deleteResources = new HashSet<Resource>();
		for (Resource rd : specification.getArchitecture()) {
			if (!resources.contains(rd)) {
				deleteResources.add(rd);
			}
		}
		Set<Mapping<Task, Resource>> deleteMappings = new HashSet<Mapping<Task, Resource>>();
		Set<Task> deleteTasks = new HashSet<Task>();

		for (Task task : specification.getApplication()) {
			if (Models.isCommunication(task)) {
				Set<Resource> deleteRoutingResources = new HashSet<Resource>();

				Architecture<Resource, Link> routing = specification.getRoutings().get(task);
				for (Resource r : routing) {
					if (!resources.contains(r)) {
						deleteRoutingResources.add(r);
					}
				}
				for (Resource r : deleteRoutingResources) {
					routing.removeVertex(r);
				}
				deleteTasks.add(task); // remove all communications
				/*
				 * if(routing.getVertexCount() == 0){ deleteTasks.add(task); }
				 */

			} else if (Models.isProcess(task)) {
				boolean keep = false;
				for (Mapping<Task, Resource> mapping : specification.getMappings().get(task)) {
					if (resources.contains(mapping.getTarget())) {
						keep = true;
					} else {
						deleteMappings.add(mapping);
					}
				}
				if (!keep) {
					deleteTasks.add(task);
				}
			}
		}

		specification.getMappings().removeAll(deleteMappings);
		specification.getApplication().removeVertices(deleteTasks);
		specification.getArchitecture().removeVertices(deleteResources);

	}

	public static void filter(Specification specification, Collection<Function<Task, Dependency>> functions) {
		Set<Task> keep = new HashSet<Task>();
		for (Function<Task, Dependency> function : functions) {
			for (Task task : function) {
				keep.add(task);
			}
		}

		Set<Task> removeTasks = new HashSet<Task>();
		Set<Mapping<Task, Resource>> removeMappings = new HashSet<Mapping<Task, Resource>>();

		for (Task task : specification.getApplication()) {
			if (!keep.contains(task)) {
				removeTasks.add(task);
			}
		}
		for (Mapping<Task, Resource> mapping : specification.getMappings()) {
			if (!keep.contains(mapping.getSource())) {
				removeMappings.add(mapping);
			}
		}

		for (Task task : removeTasks) {
			if (Models.isCommunication(task)) {
				specification.getRoutings().remove(task);
			}
			specification.getApplication().removeVertex(task);
		}
		for (Mapping<Task, Resource> mapping : removeMappings) {
			specification.getMappings().remove(mapping);
		}

		Set<Resource> keepResources = new HashSet<Resource>();
		Set<Resource> removeResources = new HashSet<Resource>();

		for (Mapping<Task, Resource> mapping : specification.getMappings()) {
			keepResources.add(mapping.getTarget());
		}
		for (Architecture<Resource, Link> routing : specification.getRoutings().getRoutings()) {
			for (Resource resouce : routing) {
				keepResources.add(resouce);
			}
		}
		for (Resource resource : specification.getArchitecture()) {
			if (!keepResources.contains(resource)) {
				removeResources.add(resource);
			}
		}
		for (Resource resource : removeResources) {
			specification.getArchitecture().removeVertex(resource);
		}

		for (Architecture<Resource, Link> routing : specification.getRoutings().getRoutings()) {
			List<Resource> remove = new ArrayList<Resource>();

			for (Resource resource : routing) {
				if (!specification.getArchitecture().containsVertex(resource)) {
					remove.add(resource);
				}
			}

			for (Resource resource : remove) {
				routing.removeVertex(resource);
			}
		}

	}

	public static void filterByFunctionName(Specification specification, Collection<String> functions) {
		Collection<Function<Task, Dependency>> funcs = new HashSet<Function<Task, Dependency>>();
		for (String f : functions) {
			funcs.add(specification.getApplication().getFunction(f));
		}
		filter(specification, funcs);
	}

	public static void filterByFunctionName(Specification specification, String... functions) {
		Collection<String> funcs = new HashSet<String>();
		for (String s : functions) {
			funcs.add(s);
		}
		filterByFunctionName(specification, funcs);
	}

}
