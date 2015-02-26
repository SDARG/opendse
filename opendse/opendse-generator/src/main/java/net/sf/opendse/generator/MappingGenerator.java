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

import static net.sf.opendse.model.Models.filterProcesses;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@code MappingGenerator} contains several methods to generate and
 * transform mappings.
 * 
 * @author lukasiewycz
 * 
 */
public class MappingGenerator extends Generator {

	protected final IdProvider mappingId;

	/**
	 * Constructs a {@code MappingGenerator} with a random seed.
	 */
	public MappingGenerator() {
		this(System.currentTimeMillis());
	}

	/**
	 * Constructs a {@code MappingGenerator} with a given seed.
	 */
	public MappingGenerator(long seed) {
		this(seed, new IdProvider("m"));
	}

	/**
	 * Constructs a {@code MappingGenerator}.
	 * 
	 * @param seed
	 *            the seed
	 * @param mappingId
	 *            the provider for the mapping ids
	 */
	public MappingGenerator(long seed, IdProvider mappingId) {
		super(new Random(seed));
		this.mappingId = mappingId;
	}

	/**
	 * Creates mappings.
	 * 
	 * @param application
	 *            the application
	 * @param architecture
	 *            the architecture
	 * @param min
	 *            the minimal number of mappings per task
	 * @param max
	 *            the maximal number of mappings per task
	 * @return the mappings
	 */
	public Mappings<Task, Resource> create(Application<Task, Dependency> application,
			Architecture<Resource, Link> architecture, int min, int max) {
		return create(application, architecture.getVertices(), min, max);
	}

	/**
	 * Creates mappings.
	 * 
	 * @param application
	 *            the application
	 * @param resources
	 *            the resources
	 * @param min
	 *            the minimal number of mappings per task
	 * @param max
	 *            the maximal number of mappings per task
	 * @return the mappings
	 */
	public Mappings<Task, Resource> create(Application<Task, Dependency> application, Collection<Resource> resources,
			int min, int max) {
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();

		List<Task> tasks = new ArrayList<Task>(application.getVertices());
		List<Resource> res = new ArrayList<Resource>(new HashSet<Resource>(resources));

		for (Task task : filterProcesses(tasks)) {
			int x = rand(min, max);
			List<Resource> targets = new ArrayList<Resource>();

			while (targets.size() < x) {
				Resource target = rand(res);
				targets.add(target);
			}

			for (Resource target : targets) {
				Mapping<Task, Resource> mapping = new Mapping<Task, Resource>(mappingId.next(), task, target);
				mappings.add(mapping);
			}
		}

		return mappings;
	}

	public void annotateAttribute(Mappings<Task, Resource> mappings, String attribute, int min, int max) {

		for (Mapping<Task, Resource> mapping : mappings.getAll()) {
			mapping.setAttribute(attribute, rand(min, max));
		}

	}

}
