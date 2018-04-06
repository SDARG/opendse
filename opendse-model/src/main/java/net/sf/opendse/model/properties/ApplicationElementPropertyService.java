package net.sf.opendse.model.properties;

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
		ACTIVATION_MODE("activation mode");
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

	protected static void checkElement(Element element) {
		if (!(element instanceof Dependency || element instanceof Task)) {
			throw new IllegalArgumentException("The element " + element.getId() + " is not an application element.");
		}
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
