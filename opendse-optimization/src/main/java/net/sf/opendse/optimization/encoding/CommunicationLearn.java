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
package net.sf.opendse.optimization.encoding;

import static net.sf.opendse.encoding.firm.variables.Variables.n;
import static net.sf.opendse.encoding.firm.variables.Variables.p;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.opendse.encoding.firm.variables.CommunicationVariable;
import net.sf.opendse.model.ICommunication;
import net.sf.opendse.optimization.encoding.common.BinaryReachability;

import org.apache.commons.collections15.functors.InstantiateFactory;
import org.apache.commons.collections15.map.LazyMap;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Literal;

/**
 * The {@code CommunicationLearn} learns units from the communication variables.
 * This is done by a {@link BinaryReachability} search.
 * 
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class CommunicationLearn {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Literal> learn(Collection<Constraint> constraints) {
		Set<Literal> learned = new HashSet<Literal>();

		Map<Object, Set<Constraint>> map = LazyMap.decorate(new HashMap<Object, Set<Constraint>>(),
				new InstantiateFactory(HashSet.class));

		Map<ICommunication, Set<CommunicationVariable>> cvars = LazyMap.decorate(
				new HashMap<ICommunication, Set<CommunicationVariable>>(), new InstantiateFactory(HashSet.class));

		for (Constraint constraint : constraints) {
			for (Literal literal : constraint.getLiterals()) {
				Object var = literal.variable();
				map.get(var).add(constraint);
				if (var instanceof CommunicationVariable) {
					CommunicationVariable cvar = (CommunicationVariable) var;
					cvars.get(cvar.getCommunication()).add(cvar);
				}
			}
		}

		for (ICommunication communication : cvars.keySet()) {
			// System.out.println("communication "+communication);
			Set<Constraint> cs = new HashSet<Constraint>();

			Set<Object> visited = new HashSet<Object>();
			Set<Object> vars = new HashSet<Object>(cvars.get(communication));

			final int depth = 2;

			for (int i = 0; i < depth; i++) {
				Set<Constraint> css = new HashSet<Constraint>(); // new
																	// constraints
				for (Object var : vars) {
					visited.add(var);
					css.addAll(map.get(var));
				}
				css.removeAll(cs);
				cs.addAll(css);
				vars.clear();

				if (i - 1 < depth) {
					for (Constraint constraint : css) {
						for (Literal lit : constraint.getLiterals()) {
							Object var = lit.variable();
							if (!(var instanceof CommunicationVariable) && !visited.contains(var)) {
								vars.add(var); // new variables
							}
						}
					}
				}
			}

			Set<Literal> lits = new HashSet<Literal>();
			for (CommunicationVariable var : cvars.get(communication)) {
				lits.add(p(var));
				lits.add(n(var));
			}

			BinaryReachability binaryReachability = new BinaryReachability();
			Set<Literal> learn = binaryReachability.search(cs, lits);

			learned.addAll(learn);
		}

		return learned;
	}
}
