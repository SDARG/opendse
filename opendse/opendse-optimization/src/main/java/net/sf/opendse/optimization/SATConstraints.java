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
package net.sf.opendse.optimization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.encoding.CommunicationLearn;
import net.sf.opendse.optimization.encoding.Encoding;
import net.sf.opendse.optimization.encoding.RoutingFilter;
import net.sf.opendse.optimization.encoding.common.ConstraintPreprocessing;

import org.opt4j.core.start.Constant;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The {@code SATConstraints} objects initializes constraints and applies the
 * preprocessing.
 * 
 * @author martin.lukasiewycz
 *
 */
@Singleton
public class SATConstraints {

	protected final SpecificationWrapper specificationWrapper;
	protected final List<Constraint> constraints = new ArrayList<Constraint>();
	protected final List<Object> variables = new ArrayList<Object>();
	protected final ConstraintPreprocessing pp;
	protected final boolean usePreprocessing;
	protected boolean isInit = false;
	protected Encoding encoding;

	@Inject
	public SATConstraints(SpecificationWrapper specificationWrapper, Encoding encoding, @Constant(value = "preprocessing", namespace = SATConstraints.class) boolean usePreprocessing) {
		this(specificationWrapper, encoding, new ConstraintPreprocessing(true, true,
				new Encoding.VariableComparator(), null, true), usePreprocessing);
		
	}

	public SATConstraints(SpecificationWrapper specificationWrapper, Encoding encoding, ConstraintPreprocessing pp, boolean usePreprocessing) {
		super();
		this.specificationWrapper = specificationWrapper;
		this.encoding = encoding;
		this.pp = pp;
		this.usePreprocessing = usePreprocessing;
	}

	public synchronized List<Constraint> getConstraints() {
		if (!isInit) {
			init();
		}
		return constraints;
	}

	public synchronized List<Object> getVariables() {
		if (!isInit) {
			init();
		}
		return variables;
	}

	public synchronized void init() {
		if (!isInit) {
			Specification specification = specificationWrapper.getSpecification();
			RoutingFilter.filter(specification);

			Collection<Constraint> constraints = encoding.toConstraints(specification);

			CommunicationLearn clearn = new CommunicationLearn();
			Set<Literal> learned = clearn.learn(constraints);
			for (Literal literal : learned) {
				Constraint constraint = new Constraint("=", 1);
				constraint.add(literal);
				constraints.add(constraint);
			}
			/*
			 * for (Constraint constraint : constraints) {
			 * System.out.println(constraint); }
			 */
			// this.constraints.addAll(constraints);
			
			if(usePreprocessing){
				this.constraints.addAll(pp.process(constraints));
			} else {
				this.constraints.addAll(constraints);
			}

			Set<Object> variables = new HashSet<Object>();
			for (Constraint constraint : this.constraints) {
				for (Literal literal : constraint.getLiterals()) {
					variables.add(literal.variable());
				}
			}
			this.variables.addAll(variables);

			isInit = true;
		}
	}

	public synchronized Model decorate(Model model) {
		if (!isInit) {
			init();
		}
		
		if(usePreprocessing){
			return pp.decorate(model);
		} else {
			return model;
		}		
	}

	public ConstraintPreprocessing getPreprocessing() {
		return pp;
	}

}
