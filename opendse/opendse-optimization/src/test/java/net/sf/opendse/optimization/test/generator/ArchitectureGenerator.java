package net.sf.opendse.optimization.test.generator;

import java.util.Random;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@code ArchitectureGenerator} contains several methods to generate and
 * transform architectures.
 * 
 * @author lukasiewycz
 * 
 */
public class ArchitectureGenerator extends Generator {

	protected final IdProvider resourceId;
	protected final IdProvider linkId;

	/**
	 * Constructs an {@code ArchitectureGenerator} with a random seed.
	 */
	public ArchitectureGenerator() {
		this(System.currentTimeMillis());
	}

	/**
	 * Constructs an {@code ArchitectureGenerator} with a given seed.
	 */
	public ArchitectureGenerator(long seed) {
		this(seed, new IdProvider("r"), new IdProvider("l"));
	}

	/**
	 * Constructs an {@code ArchitectureGenerator}.
	 * 
	 * @param seed
	 *            the seed
	 * @param resourceId
	 *            the provider for the resource ids
	 * @param linkId
	 *            the provider for the link ids
	 */
	public ArchitectureGenerator(long seed, IdProvider resourceId, IdProvider linkId) {
		super(new Random(seed));
		this.resourceId = resourceId;
		this.linkId = linkId;
	}

	/**
	 * Constructs a star architecture.
	 * 
	 * @param depth
	 *            the depths of the branches
	 * @param branches
	 *            the number of branches
	 * @return the architecture
	 */
	public Architecture<Resource, Link> getStar(int depth, int branches) {
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();

		Resource center = new Resource(resourceId.next());
		architecture.addVertex(center);

		for (int i = 0; i < branches; i++) {
			Resource current = center;

			for (int j = 0; j < depth; j++) {
				Resource next = new Resource(resourceId.next());
				Link edge = new Link(linkId.next());
				architecture.addEdge(edge, current, next);
				current = next;
			}
		}

		return architecture;
	}

}
