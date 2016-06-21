/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.generator;

import java.util.Arrays;
import java.util.Collection;
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

	public Architecture<Resource, Link> merge(Collection<Architecture<Resource, Link>> architectures) {
		Architecture<Resource, Link> architecture = new Architecture<Resource, Link>();
		for (Architecture<Resource, Link> arch : architectures) {
			for (Resource resource : arch.getVertices()) {
				architecture.addVertex(resource);
			}
			for (Link link : arch.getEdges()) {
				architecture.addEdge(link, arch.getEndpoints(link), arch.getEdgeType(link));
			}
		}
		return architecture;
	}

	public Architecture<Resource, Link> merge(Architecture<Resource, Link>... architectures) {
		Collection<Architecture<Resource, Link>> archs = Arrays.asList(architectures);
		return merge(archs);
	}

}
