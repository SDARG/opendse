package net.sf.opendse.optimization;

import org.opt4j.core.Genotype;
import org.opt4j.core.genotype.CompositeGenotype;
import org.opt4j.core.problem.Creator;

import com.google.inject.Inject;

public class DesignSpaceExplorationCreator implements Creator<CompositeGenotype<String, Genotype>> {

	protected final SATCreatorDecoder satCreator;
	protected final ParameterCreator parameterCreator;

	@Inject
	public DesignSpaceExplorationCreator(SATCreatorDecoder satCreator, ParameterCreator parameterCreator) {
		super();
		this.satCreator = satCreator;
		this.parameterCreator = parameterCreator;
	}

	@Override
	public CompositeGenotype<String, Genotype> create() {
		CompositeGenotype<String, Genotype> g = new CompositeGenotype<String, Genotype>();
		g.put("SAT", satCreator.create());
		g.put("PARAMETER", parameterCreator.create());
		return g;
	}

}
