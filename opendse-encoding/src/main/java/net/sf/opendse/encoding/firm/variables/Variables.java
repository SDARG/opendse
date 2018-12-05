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
package net.sf.opendse.encoding.firm.variables;

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
