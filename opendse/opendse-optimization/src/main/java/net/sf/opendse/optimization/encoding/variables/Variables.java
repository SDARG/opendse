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
package net.sf.opendse.optimization.encoding.variables;

import java.util.HashMap;
import java.util.Map;

import net.sf.opendse.model.Edge;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.Models.DirectedLink;

import org.opt4j.satdecoding.Literal;

/**
 * The {@code Variables} contains static methods to generate variable objects.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Variables {

	static Map<Literal, Literal> cache = new HashMap<Literal, Literal>();

	public static Literal p(Object obj) {
		Literal literal = new Literal(obj, true);
		if (cache.containsKey(literal)) {
			return cache.get(literal);
		} else {
			cache.put(literal, literal);
			return literal;
		}
	}

	public static Literal n(Object obj) {
		Literal literal = new Literal(obj, false);
		if (cache.containsKey(literal)) {
			return cache.get(literal);
		} else {
			cache.put(literal, literal);
			return literal;
		}
	}

	public static CR var(Task c, Resource r) {
		return new CR(c, r);
	}

	public static CLRR var(Task c, Edge l, Resource r0, Resource r1) {
		return new CLRR(c, l, r0, r1);
	}

	public static CLRR var(Task c, DirectedLink l) {
		return var(c, l.getLink(), l.getSource(), l.getDest());
	}

	public static CLRRP var(Task c, Edge l, Resource r0, Resource r1, Task p) {
		return new CLRRP(c, l, r0, r1, p);
	}

	public static CLRRP var(Task c, DirectedLink l, Task p) {
		return var(c, l.getLink(), l.getSource(), l.getDest(), p);
	}

	public static CLRRT var(Task c, Edge l, Resource r0, Resource r1, int t) {
		return new CLRRT(c, l, r0, r1, t);
	}

	public static CLRRT var(Task c, DirectedLink l, int t) {
		return var(c, l.getLink(), l.getSource(), l.getDest(), t);
	}

	public static EAVI var(Element e, String a, Object v, Integer i) {
		return new EAVI(e, a, v, i);
	}
}
