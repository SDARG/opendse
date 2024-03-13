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

	private Constraint checkForDoubleConstraintsInverseSigns(Constraint constraint, Collection<Constraint> constraints) {
		for(Constraint c : constraints) {
			boolean sameLiteral = c.get(0).getLiteral().variable() == constraint.get(0).getLiteral().variable();
			boolean sameRhs = c.getRhs() == constraint.getRhs();
			boolean unitClause = (c.size() == 1) && (constraint.size() == 1);
			boolean inverseSigns = (c.get(0).getLiteral().phase()) != (constraint.get(0).getLiteral().phase());
			boolean sameOperator = c.getOperator().equals(constraint.getOperator());
			if(sameLiteral && sameOperator && sameRhs && unitClause && inverseSigns) {
				return c;
			}
		}
		return null;
	}
	
	public synchronized void init() {
		if (!isInit) {
			Specification specification = specificationWrapper.getSpecification();
			RoutingFilter.filter(specification);

			Collection<Constraint> constraints = encoding.toConstraints(specification);

			System.out.println("number of constraints from spec: "+constraints.size());
			assert(constraints.size()==307);
			int emptyConstraints = 0;
			int contradictingConstraints = 0;
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			for(Constraint c : constraints) {
				if(!c.getLiterals().iterator().hasNext()) {
					emptyConstraints++;
					System.out.println("empty constraint: "+c);
				}
				Constraint temp = checkForDoubleConstraintsInverseSigns(c, constraints);
				if(temp != null) {
					System.out.println("contradicting constraints "+temp+"  "+c);
					contradictingConstraints++;
				}
			}
			System.out.println("#empty constraints from spec: "+emptyConstraints);
			System.out.println("#contradicting constraints from spec: "+contradictingConstraints);
			
			
			CommunicationLearn clearn = new CommunicationLearn();
			Set<Literal> learned = clearn.learn(constraints);
			for (Literal literal : learned) {
				Constraint constraint = new Constraint("=", 1);
				constraint.add(literal);
				constraints.add(constraint);
			}
			
			emptyConstraints = 0;
			contradictingConstraints = 0;
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&");
			for(Constraint c : constraints) {
				if(!c.getLiterals().iterator().hasNext()) {
					emptyConstraints++;
					System.out.println("empty constraint: "+c);
				}
				Constraint temp = checkForDoubleConstraintsInverseSigns(c, constraints);
				if(temp != null) {
					System.out.println("contradicting constraints "+temp+"  "+c);
					contradictingConstraints++;
				}
			}
			System.out.println("#empty constraints from communication learn: "+emptyConstraints);
			System.out.println("#contradicting constraints from communication learn: "+contradictingConstraints);
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&");
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
try {
	Thread.sleep(5000);
} catch (InterruptedException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
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
