package net.sf.opendse.encoding.preprocessing;

import net.sf.opendse.encoding.SpecificationPreprocessor;
import net.sf.opendse.model.Specification;

public abstract class SpecificationPreprocessorComposable implements SpecificationPreprocessor, Comparable<SpecificationPreprocessorComposable> {

	@Override
	public abstract void preprocessSpecification(Specification userSpecification);

	/**
	 * Returns the priority of the preprocessor. Higher priority means that the
	 * preprocessor is activated later. 0 is the smallest priority value.
	 * 
	 * @return the priority of the preprocessor
	 */
	public int getPriority() {
		return 0;
	}
	
	@Override
	public int compareTo(SpecificationPreprocessorComposable o) {
		int thisPrio = getPriority();
		int otherPrio = getPriority();
		if (thisPrio < otherPrio) {
			return -1;
		}
		if (thisPrio == otherPrio) {
			return 0;
		}
		return 1;
	}
}
