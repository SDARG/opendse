package net.sf.opendse.model.parameter;

import java.util.Arrays;
import java.util.List;

/**
 * The {@code Parameters} provides methods to create {@link Parameter}
 * attributes.
 * 
 * @author Martin Lukasiewycz
 * 
 */
public class Parameters {

	/**
	 * Constructs a {@link ParameterSelect}.
	 * 
	 * @param def
	 *            the default value
	 * @param select
	 *            the list of elements
	 * @return the parameter object
	 */
	public static ParameterSelect select(Object def, Object... select) {
		ParameterSelect parameter = new ParameterSelect(def, null, Arrays.asList(select));
		return parameter;
	}

	/**
	 * Constructs a {@link ParameterSelect}.
	 * 
	 * @param reference
	 *            the reference parameter
	 * @param def
	 *            the default value
	 * @param select
	 *            the list of elements
	 * @return the parameter object
	 */
	public static ParameterSelect selectRef(String reference, Object def, Object... select) {
		ParameterSelect parameter = new ParameterSelect(def, reference, Arrays.asList(select));
		return parameter;
	}

	/**
	 * Constructs a {@link ParameterSelect}.
	 * 
	 * @param reference
	 *            the reference parameter
	 * @param def
	 *            the default value
	 * @param select
	 *            the list of elements
	 * @return the parameter object
	 */
	public static ParameterSelect selectRefList(String reference, Object def, List<Object> select) {
		ParameterSelect parameter = new ParameterSelect(def, reference, select);
		return parameter;
	}

	/**
	 * Constructs a {@link ParameterRange}.
	 * 
	 * @param def
	 *            the default value
	 * @param lb
	 *            the lower bound
	 * @param ub
	 *            the upper bound
	 * @return the parameter object
	 */
	public static ParameterRange range(double def, double lb, double ub) {
		return range(def, lb, ub, 0);
	}

	/**
	 * Constructs a {@link ParameterRange}.
	 * 
	 * @param def
	 *            the default value
	 * @param lb
	 *            the lower bound
	 * @param ub
	 *            the upper bound
	 * @param granularity
	 *            the granularity (step-size)
	 * @return the parameter object
	 */
	public static ParameterRange range(double def, double lb, double ub, double granularity) {
		ParameterRange range = new ParameterRange(def, lb, ub, granularity);
		return range;
	}

	/**
	 * Constructs a {@link ParameterUniqueID}.
	 * 
	 * @param def
	 *            the default value
	 * @param identifier
	 *            the identifier
	 * @return the parameter object
	 */
	public static ParameterUniqueID uniqueID(int def, String identifier) {
		return new ParameterUniqueID(def, identifier);
	}

}
