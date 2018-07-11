package net.sf.opendse.optimization.io;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Attributes;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.LinkTypes;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.ResourceTypes;
import net.sf.opendse.model.Specification;
import net.sf.opendse.model.SpecificationTypeBased;
import net.sf.opendse.model.Task;

/**
 * Basic {@link Transformer} from a {@link SpecificationTypeBased} to a
 * {@link Specification}. The default {@link Architecture} is constructed by
 * connecting one {@link Resource} of each type in the {@link ResourceTypes} to
 * an unlimited-capacity on-chip bus with {@link Link}s from the
 * {@link LinkTypes}.
 * 
 * @author Valentina Richthammer
 */
public class SpecificationTransformerTypeBased<T extends SpecificationTypeBased, S extends Specification>
		implements Transformer<T, S> {

	protected final String CONNECTOR = "_";
	protected Map<Resource, List<Resource>> typeMap;

	/**
	 * Transforms a {@link SpecificationTypeBased} into a {@link Specification}.
	 * 
	 * @param T
	 *            the {@link SpecificationTypeBased}
	 * @return the {@link Specification}
	 */
	@Override
	public S transform(T typeBasedSpec) {

		Architecture<Resource, Link> architecture = generateArchitecture(typeBasedSpec.getResourceTypes(),
				typeBasedSpec.getLinkTypes());
		Mappings<Task, Resource> archMappings = generateMappings(typeBasedSpec.getMappings(), architecture);

		return (S) new Specification(typeBasedSpec.getApplication(), architecture, archMappings);
	}

	/**
	 * Creates a default architecture from {@link ResourceTypes} and
	 * {@link LinkTypes} by connecting one resource of each type to an on-chip
	 * bus with a link from the {@link LinkTypes}.
	 * 
	 * @param resourceTypes
	 *            the {@link ResourceTypes}
	 * @param linkTypes
	 *            the {@link ResourceTypes}
	 * @return the architecture
	 */
	protected Architecture<Resource, Link> generateArchitecture(ResourceTypes<Resource> resourceTypes,
			LinkTypes<?> linkTypes) {

		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		Link linkType = linkTypes.values().iterator().next();

		// on-chip bus
		Resource bus = new Resource("bus");

		for (Resource type : resourceTypes.values()) {
			Resource instance = new Resource("r" + CONNECTOR + type.getId());

			Attributes attributes = type.getAttributes();
			for (String key : attributes.keySet()) {
				instance.setAttribute(key, attributes.getAttribute(key));
			}
			instance.setType(type.getId());

			Link link = new Link("l" + CONNECTOR + type.getId());

			Attributes linkAttributes = linkType.getAttributes();
			for (String key : linkAttributes.keySet()) {
				link.setAttribute(key, linkAttributes.getAttribute(key));
			}

			architecture.addVertex(instance);
			architecture.addEdge(link, instance, bus);
		}
		return architecture;
	}

	/**
	 * Create concrete {@link Mappings} from the type-based mappings in the
	 * {@link SpecificationTypeBased}.
	 * 
	 * @param typeMappings
	 *            the type-based mappings from the
	 *            {@link SpecificationTypeBased}
	 * @param architecture
	 *            the concrete architecture
	 * @return the mappings
	 */
	protected Mappings<Task, Resource> generateMappings(Mappings<Task, Resource> typeMappings,
			Architecture<Resource, Link> architecture) {

		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		// generate concrete mappings from type-mappings
		for (Mapping<Task, Resource> typeMapping : typeMappings) {

			Task source = typeMapping.getSource();
			Resource target = typeMapping.getTarget();

			Resource instance = architecture.getVertex("r" + CONNECTOR + target.getId());

			Mapping<Task, Resource> mapping = new Mapping<Task, Resource>(
					"m" + source.getId() + CONNECTOR + instance.getId(), source, instance);

			Attributes attributes = typeMapping.getAttributes();
			for (String key : attributes.keySet()) {
				mapping.setAttribute(key, attributes.getAttribute(key));
			}
			mappings.add(mapping);
		}
		return mappings;
	}
}
