package net.sf.opendse.encoding.interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.opendse.model.Specification;

@Singleton
public class SpecificationPostProcessorMulti implements SpecificationPostProcessor {

	protected final List<SpecificationPostProcessorComposable> postProcessorList;
	
	@Inject
	public SpecificationPostProcessorMulti() {
		this.postProcessorList = new ArrayList<SpecificationPostProcessorComposable>();
	}
	
	/**
	 * Adds the given {@link SpecificationPostProcessorComposable} to the post processor list.
	 * 
	 * @param postProcessor
	 */
	public void addPostProcessor(SpecificationPostProcessorComposable postProcessor) {
		this.postProcessorList.add(postProcessor);
	}
	
	@Override
	public void postProcessImplementation(Specification implementation) {
        Collections.sort(postProcessorList);
		for (SpecificationPostProcessorComposable postProcessor : postProcessorList) {
			postProcessor.postProcessImplementation(implementation);
		}
	}
}
