package net.sf.opendse.optimization;

import org.opt4j.satdecoding.Constraint;
import org.opt4j.satdecoding.Solver;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SATIncremental {

	protected final SATConstraints satConstraints;
	protected final Solver solver;

	@Inject
	public SATIncremental(SATConstraints satConstraints, Solver solver) {
		super();
		this.satConstraints = satConstraints;
		this.solver = solver;
	}

	public void exclude(Constraint constraint) {
		Constraint c = satConstraints.getPreprocessing().processAfterInit(constraint);
		solver.addConstraint(c);
	}

}
