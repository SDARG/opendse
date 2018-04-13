package net.sf.opendse.model.properties;

import net.sf.opendse.model.Application;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Task;

/**
 * The {@link ApplicationElementPropertyService} offers convenience methods to
 * access the properties of {@link Dependency}s and {@link Task}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class ApplicationElementPropertyService extends AbstractPropertyService {

	public enum ApplicationElementAttributes {
		ACTIVATION_MODE("activation mode"), ALT_FUNCTION("alternative application"), ALT_ID("alternative id");
		protected String xmlName;

		private ApplicationElementAttributes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	public enum ActivationModes {
		STATIC("static"), ALTERNATIVE("alternative");
		protected String xmlName;

		private ActivationModes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	private ApplicationElementPropertyService() {
	}

	/**
	 * Checks whether the given element is valid to be processed by this class, that
	 * is whether it is either a {@link Task} or a {@link Dependency}. An
	 * {@link IllegalArgumentException} is thrown if the input {@link Element} is
	 * not valid.
	 * 
	 * @param element
	 *            the {@link Element} that is to be checked
	 */
	protected static void checkElement(Element element) {
		if (!(element instanceof Dependency || element instanceof Task)) {
			throw new IllegalArgumentException("The element " + element.getId() + " is not an application element.");
		}
	}

	/**
	 * Sets the parameters for an {@link Element} with an ALTERNATIVE
	 * {@link ActivationModes}. An activation of such an element is described by two
	 * parameters, the alternative function and the alternative id. At runtime,
	 * exactly one alternative id is activated for each alternative function in the
	 * overall {@link Application} graph. All elements with of the function with
	 * this id are activated, while all other elements are deactivated.
	 * 
	 * @param element
	 *            the {@link Element} with a ALTERNATIVE {@link ActivationModes}
	 * @param alternativeFunction
	 *            the name of the application function with alternative application
	 *            subgraphs
	 * @param alternativeId
	 *            the name of the application subgraph the current element is part
	 *            of
	 */
	public static void setAlternativeAttributes(Element element, String alternativeFunction, String alternativeId) {
		checkElement(element);
		if (!getActivationMode(element).equals(ActivationModes.ALTERNATIVE)) {
			throw new IllegalArgumentException(
					"The element " + element.getId() + " is not activated as an alternative.");
		}
		element.setAttribute(ApplicationElementAttributes.ALT_FUNCTION.xmlName, alternativeFunction);
		element.setAttribute(ApplicationElementAttributes.ALT_ID.xmlName, alternativeId);
	}

	/**
	 * 
	 * Returns the alternative id of the given {@link Element}. See the
	 * {@link #setAlternativeAttributes(Element, String, String)} method for a
	 * description of the alternative attributes.
	 * 
	 * @param element
	 *            the {@link Element} with a ALTERNATIVE {@link ActivationModes}
	 * @return the alternative id of the given {@link Element}. See the
	 *         {@link #setAlternativeAttributes(Element, String, String)} method for
	 *         a description of the alternative attributes.
	 */
	public static String getAlternativeId(Element element) {
		checkElement(element);
		checkAttribute(element, ApplicationElementAttributes.ALT_FUNCTION.xmlName);
		return (String) getAttribute(element, ApplicationElementAttributes.ALT_ID.xmlName);
	}

	/**
	 * Returns the alternative function of the given {@link Element}. See the
	 * {@link #setAlternativeAttributes(Element, String, String)} method for a
	 * description of the alternative attributes.
	 * 
	 * @param element
	 *            the {@link Element} with a ALTERNATIVE {@link ActivationModes}
	 * @return the alternative function of the given {@link Element}. See the
	 *         {@link #setAlternativeAttributes(Element, String, String)} method for
	 *         a description of the alternative attributes.
	 */
	public static String getAlternativeFunction(Element element) {
		checkElement(element);
		checkAttribute(element, ApplicationElementAttributes.ALT_ID.xmlName);
		return (String) getAttribute(element, ApplicationElementAttributes.ALT_FUNCTION.xmlName);
	}

	/**
	 * Returns the activation mode of the given task. Returns the STATIC activation
	 * mode if the attribute is not set.
	 * 
	 * @param element
	 *            the input {@link Element}
	 * @return the activation mode of the task
	 */
	public static ActivationModes getActivationMode(Element element) {
		checkElement(element);
		if (!isAttributeSet(element, ApplicationElementAttributes.ACTIVATION_MODE.xmlName)) {
			return ActivationModes.STATIC;
		} else {
			String attrString = element.getAttribute(ApplicationElementAttributes.ACTIVATION_MODE.xmlName);
			if (attrString.equals(ActivationModes.STATIC.xmlName)) {
				return ActivationModes.STATIC;
			} else if (attrString.equals(ActivationModes.ALTERNATIVE.xmlName)) {
				return ActivationModes.ALTERNATIVE;
			} else {
				throw new IllegalArgumentException(
						"Unknown activation mode for application element " + element.getId());
			}
		}
	}

	public static void setActivationMode(Element element, ActivationModes activationMode) {
		checkElement(element);
		element.setAttribute(ApplicationElementAttributes.ACTIVATION_MODE.xmlName, activationMode.xmlName);
	}
}
