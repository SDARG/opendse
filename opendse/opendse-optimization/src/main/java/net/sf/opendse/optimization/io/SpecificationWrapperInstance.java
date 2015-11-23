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
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.optimization.io;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.SpecificationWrapper;
import net.sf.opendse.optimization.encoding.RoutingFilter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SpecificationWrapperInstance implements SpecificationWrapper {

	protected Set<SpecificationTransformer> transformers = new TreeSet<SpecificationTransformer>(
			new Comparator<SpecificationTransformer>() {
				@Override
				public int compare(SpecificationTransformer o1, SpecificationTransformer o2) {
					return ((Integer) o1.getPriority()).compareTo(o2.getPriority());
				}
			});

	protected final Specification specification;
	private boolean init = false;

	public SpecificationWrapperInstance(Specification specification) {
		assert specification != null;
		this.specification = specification;
	}

	@Inject(optional = true)
	public void setSpecificationTransformers(Set<SpecificationTransformer> transformers) {
		this.transformers.addAll(transformers);
	}

	@Override
	public Specification getSpecification() {
		if (!init) {
			init = true;
			if (transformers != null) {
				for (SpecificationTransformer specificationTransformer : transformers) {
					System.out.println("Starting " + specificationTransformer);
					specificationTransformer.transform(specification);
				}
			}
			RoutingFilter.filter(this.specification);
		}
		return specification;
	}

}
