package net.sf.opendse.optimization.encoding.common;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections15.Predicate;
import org.opt4j.operators.crossover.Pair;
import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Constraint.Operator;
import org.opt4j.satdecoding.ContradictionException;
import org.opt4j.satdecoding.Literal;
import org.opt4j.satdecoding.Model;
import org.opt4j.satdecoding.Term;

/**
 * The {@code ConstraintPreprocessing} performs a preprocessing on constraints.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class ConstraintPreprocessing {

	protected final boolean verbose;
	protected final boolean searchUnits;
	protected final boolean searchEqualities;
	protected final Comparator<Object> eqComparator;
	protected boolean closed = false;

	static class VoidComparator implements Comparator<Object>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(Object o1, Object o2) {
			return 0;
		}
	};

	public ConstraintPreprocessing() {
		this(true, true, null, null, true);
	}

	public ConstraintPreprocessing(Comparator<Object> eqComparator) {
		this(true, true, eqComparator, null, false);
	}

	public ConstraintPreprocessing(boolean searchUnits, boolean searchEqualities,
			Comparator<Object> eqComparator, Predicate<Object> fixed, boolean verbose) {
		this.searchUnits = searchUnits;
		this.searchEqualities = searchEqualities;
		if (eqComparator == null) {
			this.eqComparator = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					return 0;
				}
			};
		} else {
			this.eqComparator = eqComparator;
		}

		this.verbose = verbose;
	}

	ConstraintNormalization normalization = new ConstraintNormalization();

	Set<Constraint> constraints = new HashSet<Constraint>();
	Map<Object, Set<Constraint>> variables = new HashMap<Object, Set<Constraint>>();

	Map<Object, Boolean> units = new HashMap<Object, Boolean>();
	Map<Object, Literal> equalities = new HashMap<Object, Literal>();

	class Stats {
		final int cons;
		final int lits;
		final int vars;

		public Stats(Collection<Constraint> constraints) {
			int lits = 0;
			Set<Object> vars = new HashSet<Object>();
			for (Constraint constraint : constraints) {
				lits += constraint.size();
				for (Literal lit : constraint.getLiterals()) {
					Object var = lit.variable();
					vars.add(var);
				}
			}
			this.cons = constraints.size();
			this.lits = lits;
			this.vars = vars.size();
		}
	}

	protected synchronized void close() {
		if (closed) {
			throw new IllegalStateException(getClass().getName()
					+ "#process can only be called once.");
		} else {
			closed = true;
		}
	}

	public Set<Object> variables() {
		Set<Object> variables = new HashSet<Object>();
		for (Constraint constraint : this.constraints) {
			for (Literal literal : constraint.getLiterals()) {
				variables.add(literal.variable());
			}
		}
		return variables;
	}

	public Collection<Constraint> process(Collection<Constraint> constraints) {
		close();

		Stats s0 = null;
		Stats s1 = null;

		if (verbose) {
			s0 = new Stats(constraints);
		}

		for (Constraint constraint : constraints) {
			if (constraint.getOperator() == Operator.EQ) {
				Pair<Constraint> pair = split(constraint);
				add(pair.getFirst());
				add(pair.getSecond());
			} else {
				add(constraint);
			}
		}

		Set<Object> originalVariables = variables();

		for (Literal unit : getUnits()) {
			propagateUnit(unit);
		}

		process();
		simplifyEqualities();
		minimizeObjects();

		// find dont care variables
		originalVariables.removeAll(variables());
		for (Object variable : originalVariables) {
			boolean u = units.containsKey(variable);
			boolean e = equalities.containsKey(variable);

			if (!u && !e) {
				Constraint constraint = new Constraint(">=", 0);
				constraint.add(new Literal(variable, true));
				this.constraints.add(constraint);
			}
		}

		if (verbose) {
			s1 = new Stats(this.constraints);

			DecimalFormat format = new DecimalFormat("#.#");
			double pcons = (double) s1.cons * 100 / s0.cons;
			double plits = (double) s1.lits * 100 / s0.lits;
			double pvars = (double) s1.vars * 100 / s0.vars;

			System.out.println("Constraints [" + format.format(pcons) + "%] " + s1.cons);
			System.out.println("Literals [" + format.format(plits) + "%] " + s1.lits);
			System.out.println("Variables [" + format.format(pvars) + "%] " + s1.vars);
			System.out.println("Units " + units.size() + " Equalities " + equalities.size());
		}

		List<Constraint> list = new ArrayList<Constraint>(this.constraints);

		constraints.clear();
		variables.clear();

		return list;

	}

	protected void simplifyEqualities() {
		// flatten equalities
		List<Object> eqvars = new ArrayList<Object>(equalities.keySet());

		for (Object var : eqvars) {
			Literal lit = new Literal(var, true);
			Literal eqlit = getEq(lit);
			assert (!lit.equals(eqlit));
			equalities.put(var, eqlit);
		}

		for (Object var : eqvars) {
			Literal eqlit = equalities.get(var);
			Object eqvar = eqlit.variable();

			if (units.containsKey(eqvar)) {

				boolean phase = (units.get(eqvar) == eqlit.phase());
				units.put(var, phase);
				equalities.remove(var);
			}
		}
	}

	protected void minimizeObjects() {

		Map<Literal, Literal> lits = new HashMap<Literal, Literal>();
		Map<Literal, Map<Integer, Term>> map = new HashMap<Literal, Map<Integer, Term>>();

		for (Constraint constraint : constraints) {
			for (int i = 0; i < constraint.size(); i++) {
				Term term = constraint.get(i);
				Literal lit = term.getLiteral();
				int coeff = term.getCoefficient();

				if (lits.containsKey(lit)) {
					lit = lits.get(lit);
					term = new Term(coeff, lit);
				} else {
					lits.put(lit, lit);
				}

				Map<Integer, Term> terms = map.get(lit);
				if (terms == null) {
					terms = new HashMap<Integer, Term>();
					map.put(lit, terms);
				}
				if (terms.containsKey(coeff)) {
					term = terms.get(coeff);
				} else {
					terms.put(coeff, term);
				}
				constraint.set(i, term);
			}
		}
	}

	protected Literal getEq(Literal lit) {
		Object v1 = lit.variable();
		boolean p1 = lit.phase();

		if (equalities.containsKey(v1)) {
			Literal l2 = equalities.get(v1);
			if (!p1) {
				return getEq(l2.negate());
			} else {
				return getEq(l2);
			}
		} else {
			return lit;
		}
	}

	protected void process() {
		Set<Constraint> adds = new HashSet<Constraint>(constraints);
		Collection<Constraint> learned = null;
		do {
			Collection<Constraint> cs = new HashSet<Constraint>(this.constraints);
			learned = learnFrom(cs);

			adds.clear();
			adds.addAll(learned);

		} while (!learned.isEmpty());

	}

	protected Collection<Constraint> learnFrom(Collection<Constraint> constraints) {
		List<Constraint> set = new ArrayList<Constraint>();

		if (searchUnits) {
			for (Constraint constraint : constraints) {
				if (this.constraints.contains(constraint)) {
					set.addAll(learnUnit(constraint));
				}
			}
		}
		for (Constraint constraint : constraints) {

			if (this.constraints.contains(constraint)) {
				set.addAll(simplify(constraint));
			}
		}
		if (searchEqualities) {
			for (Constraint constraint : constraints) {
				if (this.constraints.contains(constraint)) {
					set.addAll(learnEquality(constraint));
				}
			}
		}

		return set;
	}

	public Constraint processAfterInit(Constraint constraint) {

		int rhs = constraint.getRhs();
		Constraint cs = new Constraint();

		for (Term term : constraint) {
			int coeff = term.getCoefficient();
			Literal lit = term.getLiteral();
			Object var = lit.variable();
			boolean phase = lit.phase();

			if (units.containsKey(var)) {
				if (units.get(var) == phase) {
					rhs -= coeff;
				}
			} else if (equalities.containsKey(var)) {
				Literal l2 = getEq(lit);
				Term t = new Term(coeff, l2);
				cs.add(t);
			} else {
				cs.add(term);
			}
		}

		cs.setOperator(constraint.getOperator());
		cs.setRhs(rhs);
		return cs;
	}

	protected Collection<Constraint> simplify(Constraint constraint) {
		List<Constraint> set = new ArrayList<Constraint>();

		int sum = 0;
		int suma = 0;
		int mincoeff = constraint.get(0).getCoefficient();
		int rhs = constraint.getRhs();

		for (Term term : constraint) {
			int coeff = term.getCoefficient();
			if (coeff < rhs) {
				sum += coeff;
			}
			suma += coeff;
		}

		if (suma - mincoeff < rhs) {
			// all terms must be sat
			remove(constraint);

			for (Term term : constraint) {
				Literal lit = term.getLiteral();
				addUnits(lit);
				set.addAll(propagateUnit(lit));
			}

		} else if (0 < sum && sum < rhs) {
			// small terms have no effect
			remove(constraint);

			while (true) {
				Term term = constraint.get(0);
				if (term.getCoefficient() < rhs) {
					constraint.remove(0);
				} else {
					break;
				}
			}

			if (add(constraint)) {
				set.add(constraint);
			}
		}

		return set;
	}

	protected Collection<Constraint> learnUnit(Constraint constraint) {
		List<Constraint> set = new ArrayList<Constraint>();

		int size = constraint.size();

		assert (!constraint.isEmpty()) : constraint;
		if (size == 1) {
			// Learn unit
			Term term = constraint.get(0);
			assert (term.getCoefficient() == 1);
			assert (constraint.getRhs() == 1);

			Literal lit = term.getLiteral();
			addUnits(lit);
			set.addAll(propagateUnit(lit));
		}

		return set;
	}

	protected Collection<Constraint> propagateUnit(Literal lit) {
		Object var = lit.variable();
		List<Constraint> added = new ArrayList<Constraint>();

		List<Constraint> constraints = new ArrayList<Constraint>();
		if (variables.containsKey(var)) {
			constraints.addAll(variables.get(var));
		}

		for (Constraint constraint : constraints) {
			remove(constraint);

			int rhs = constraint.getRhs();
			for (int i = 0; i < constraint.size(); i++) {
				Term term = constraint.get(i);
				if (term.getLiteral().equals(lit)) {
					constraint.remove(term);
					rhs -= term.getCoefficient();
				} else if (term.getLiteral().equals(lit.negate())) {
					constraint.remove(term);
				}

			}
			constraint.setRhs(rhs);

			if (add(constraint)) {
				added.add(constraint);
			}
		}

		return added;
	}

	protected Collection<Constraint> learnEquality(Constraint constraint) {
		List<Constraint> set = new ArrayList<Constraint>();

		int size = constraint.size();

		assert (!constraint.isEmpty()) : constraint;
		if (size == 2) {
			// Learn unit
			Term t1 = constraint.get(0);
			Term t2 = constraint.get(1);
			Object v1 = t1.getLiteral().variable();
			Object v2 = t2.getLiteral().variable();
			learnEquality(v1, v2);
		}

		return set;
	}

	protected Collection<Constraint> learnEquality(Object v1, Object v2) {
		List<Constraint> learned = new ArrayList<Constraint>();

		Set<Constraint> set = new HashSet<Constraint>(variables.get(v1));
		set.retainAll(variables.get(v2));

		boolean t00 = true;
		boolean t01 = true;
		boolean t10 = true;
		boolean t11 = true;

		Literal l1t = new Literal(v1, true);
		Literal l1f = l1t.negate();
		Literal l2t = new Literal(v2, true);
		Literal l2f = l2t.negate();

		for (Constraint constraint : set) {
			Boolean t00v = apply(constraint, l1f, l2f);
			Boolean t01v = apply(constraint, l1f, l2t);
			Boolean t10v = apply(constraint, l1t, l2f);
			Boolean t11v = apply(constraint, l1t, l2t);

			t00 = t00 && (t00v != null ? t00v : true);
			t01 = t01 && (t01v != null ? t01v : true);
			t10 = t10 && (t10v != null ? t10v : true);
			t11 = t11 && (t11v != null ? t11v : true);
		}

		int ones = (t00 ? 1 : 0) + (t01 ? 1 : 0) + (t10 ? 1 : 0) + (t11 ? 1 : 0);

		if (ones == 2) {
			if (t00 && t11) {
				learned.addAll(propagateEquality(l1t, l2t));
			} else if (t10 && t01) {
				learned.addAll(propagateEquality(l1t, l2f));
			}
		}

		return learned;
	}

	/**
	 * @param l1
	 *            keep
	 * @param l2
	 *            replace
	 * @return
	 */
	protected Collection<Constraint> propagateEquality(Literal l1, Literal l2) {
		Object v1 = l1.variable();
		Object v2 = l2.variable();

		if (eqComparator.compare(v1, v2) <= 0) {

			List<Constraint> list = new ArrayList<Constraint>();

			Literal lit = new Literal(v1, l1.phase() == l2.phase());
			equalities.put(v2, lit);

			List<Constraint> constraints = new ArrayList<Constraint>(variables.get(v2));

			for (Constraint constraint : constraints) {
				remove(constraint);

				for (int i = 0; i < constraint.size(); i++) {
					Term term = constraint.get(i);
					if (term.getLiteral().variable().equals(v2)) {
						Literal l = term.getLiteral().phase() ? lit : lit.negate();
						Term t = new Term(term.getCoefficient(), l);
						constraint.set(i, t);
					}
				}

				if (add(constraint)) {
					list.add(constraint);
				}
			}

			return list;
		} else {
			return propagateEquality(l2, l1);
		}
	}

	protected void remove(Constraint constraint) {
		constraints.remove(constraint);
		for (Literal lit : constraint.getLiterals()) {
			Object var = lit.variable();
			Set<Constraint> set = variables.get(var);
			set.remove(constraint);
			if (set.isEmpty()) {
				variables.remove(var);
			}
		}
	}

	protected boolean add(Constraint constraint) {
		normalization.normalize(constraint);
		int rhs = constraint.getRhs();

		if (constraint.getRhs() > 0) {
			int sum = 0;
			for (int coeff : constraint.getCoefficients()) {
				sum += coeff;
			}
			if (sum < rhs) {
				System.err.println("contradiction " + constraint);
				throw new ContradictionException();
			} else {
				constraints.add(constraint);

				for (Literal lit : constraint.getLiterals()) {
					Object var = lit.variable();
					if (!variables.containsKey(var)) {
						variables.put(var, new HashSet<Constraint>());
					}
					variables.get(var).add(constraint);
				}
				return true;
			}

		} // else trivially satisfied, do not add
		return false;
	}

	protected void add(Collection<Constraint> constraints) {
		for (Constraint constraint : constraints) {
			add(constraint);
		}
	}

	protected Pair<Constraint> split(Constraint constraint) {
		assert (constraint.getOperator() == Operator.EQ);
		Constraint ge = constraint;
		Constraint le = constraint.copy();

		ge.setOperator(Operator.GE);
		le.setOperator(Operator.LE);

		Pair<Constraint> pair = new Pair<Constraint>(ge, le);
		return pair;

	}

	public void addUnits(Literal... literals) {

		for (Literal literal : literals) {
			boolean phase = literal.phase();
			Object var = literal.variable();

			Boolean p = units.get(var);
			if (p == null) {
				units.put(var, phase);
			} else if (p != phase) {
				System.err.print(var + " to " + p + " (is already " + !phase + ")");
				throw new ContradictionException();
			} // else p=phase
		}
	}

	public Collection<Literal> getUnits() {
		Collection<Literal> list = new ArrayList<Literal>();
		for (Entry<Object, Boolean> entry : units.entrySet()) {
			list.add(new Literal(entry.getKey(), entry.getValue()));
		}
		return list;
	}

	class ModelDecorator extends Model {

		final Model model;

		protected ModelDecorator(Model model) {
			this.model = model;
		}

		@Override
		public Boolean get(Object var) {
			assert (model != null);
			Boolean phase = model.get(var);

			if (phase != null) {
				return phase;
			} else {
				if (units.containsKey(var)) {
					return units.get(var);
				} else if (equalities.containsKey(var)) {
					Literal lit = equalities.get(var);

					Boolean b0 = lit.phase();
					Boolean b1 = get(lit.variable());

					if (b1 == null) {
						return null;
					} else {
						return (b0 == b1);
					}
				} else {
					return null;
				}
			}
		}

		private static final long serialVersionUID = 1L;
	}

	public ModelDecorator decorate(Model model) {
		return new ModelDecorator(model);
	}

	protected Boolean apply(Constraint constraint, Literal l1, Literal l2) {
		assert (constraint.getOperator() == Operator.GE) : constraint;

		Object v1 = l1.variable();
		Object v2 = l2.variable();

		int unknown = 0;
		int sum = 0;
		for (Term term : constraint) {
			Literal literal = term.getLiteral();
			Object variable = literal.variable();
			int coefficient = term.getCoefficient();

			if (!variable.equals(v1) && !variable.equals(v2)) {
				unknown += coefficient;
			} else if (literal.equals(l1) || literal.equals(l2)) {
				sum += coefficient;
			} // else unsat term
		}

		int rhs = constraint.getRhs();

		if (sum + unknown < rhs) {
			return false;
		} else if (sum >= rhs) {
			return true;
		} else {
			return null;
		}
	}
}
