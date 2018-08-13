package net.sf.opendse.encoding.interpreter;

import org.opt4j.satdecoding.Model;

import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.encoding.Interpreter;

public abstract class InterpreterAbstract implements Interpreter {

	protected final SpecificationPostProcessor postProcessor;

	public InterpreterAbstract(SpecificationPostProcessor postProcessor) {
		this.postProcessor = postProcessor;
	}

	@Override
	public final Specification toImplementation(Specification specification, Model model) {
		Specification decodedSpec = decodeModel(specification, model);
		postProcessor.postProcessImplementation(decodedSpec);
		return decodedSpec;
	}

	/**
	 * Generates the {@link Specification} created based on the variable assignment
	 * contained in the {@link Model} and the {@link Specification} provided by the
	 * user.
	 * 
	 * @param specification
	 *            the {@link Specification} provided by the user
	 * @param model
	 *            the {@link Model} containing a valid variable assignment
	 * @return the decoded implementation-{@link Specification}
	 */
	public abstract Specification decodeModel(Specification specification, Model model);

}
