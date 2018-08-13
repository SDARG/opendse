package net.sf.opendse.encoding.preprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import net.sf.opendse.encoding.SpecificationPreprocessor;
import net.sf.opendse.model.Specification;

/**
 * The {@link SpecificationPreprocessorMulti} is used to use multiple
 * {@link SpecificationPreprocessor}s during one exploration. It also allows to
 * establish an order of the preprocessing.
 * 
 * @author smirnov
 *
 */
@Singleton
public class SpecificationPreprocessorMulti implements SpecificationPreprocessor {

	protected final List<SpecificationPreprocessorComposable> preprocessorList;

	@Inject
	public SpecificationPreprocessorMulti() {
		preprocessorList = new ArrayList<SpecificationPreprocessorComposable>();
	}

	@Override
	public void preprocessSpecification(Specification userSpecification) {
		Collections.sort(preprocessorList);
		for (SpecificationPreprocessorComposable preprocessor : preprocessorList) {
			preprocessor.preprocessSpecification(userSpecification);
		}
	}

	/**
	 * Adds the given {@link SpecificationPreprocessorComposable} to the list while
	 * respecting its priority.
	 * 
	 * @param preprocessor
	 *            the preprocessor to add to the list
	 */
	public void addPreprocessor(SpecificationPreprocessorComposable preprocessor) {
		preprocessorList.add(preprocessor);
	}

}
