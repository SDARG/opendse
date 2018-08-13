package net.sf.opendse.encoding.interpreter;

public abstract class SpecificationPostProcessorComposable
		implements SpecificationPostProcessor, Comparable<SpecificationPostProcessorComposable> {

	@Override
	public int compareTo(SpecificationPostProcessorComposable o) {
		int ownPrio = getPriority();
		int otherPrio = o.getPriority();
		if (ownPrio < otherPrio) {
			return -1;
		}
		if (ownPrio == otherPrio) {
			return 0;
		}
		return 1;
	}

	/**
	 * Returns the priority of the post processor. Post processors with a lower
	 * priority are executed first.
	 * 
	 * @return
	 */
	public int getPriority() {
		return 0;
	}

}
