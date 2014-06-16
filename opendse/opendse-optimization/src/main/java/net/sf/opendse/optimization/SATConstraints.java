/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
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

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Model;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SATConstraints {

	protected final SpecificationWrapper specificationWrapper;
	protected final List<Constraint> constraints = new ArrayList<Constraint>();
	protected final List<Object> variables = new ArrayList<Object>();
	protected final ConstraintPreprocessing pp = new ConstraintPreprocessing(true, true,
			new Encoding.VariableComparator(), null, true);
	protected boolean isInit = false;
	protected Encoding encoding;

	@Inject
	public SATConstraints(SpecificationWrapper specificationWrapper, Encoding encoding) {
		super();
		this.specificationWrapper = specificationWrapper;
		this.encoding = encoding;
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
			/*for (Constraint constraint : constraints) {
				System.out.println(constraint);
			}*/
			// this.constraints.addAll(constraints);
			this.constraints.addAll(pp.process(constraints));

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
		// return model;
		return pp.decorate(model);
	}
	
	public ConstraintPreprocessing getPreprocessing(){
		return pp;
	}

}
