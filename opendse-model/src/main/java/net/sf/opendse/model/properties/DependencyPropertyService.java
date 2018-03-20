package net.sf.opendse.model.properties;

import net.sf.opendse.model.Dependency;

/**
 * offers convenience methods to access the properties of dependency elements
 * 
 * @author Fedor Smirnov
 *
 */
public class DependencyPropertyService extends AbstractPropertyService {

	public enum DependencyAttributes {
		ACTIVATION_MODE(TaskPropertyService.TaskAttributes.ACTIVATION_MODE.xmlName);
		protected String xmlName;

		private DependencyAttributes(String xmlName) {
			this.xmlName = xmlName;
		}

		public String getXmlName() {
			return this.xmlName;
		}
	}

	public enum ActivationModes {
		STATIC(TaskPropertyService.ActivationModes.STATIC.xmlName), ALTERNATIVE(
				TaskPropertyService.ActivationModes.ALTERNATIVE.xmlName);
		protected String xmlName;

		private ActivationModes(String xmlName) {
			this.xmlName = xmlName;
		}

		public String getXmlName() {
			return this.xmlName;
		}
	}

	private DependencyPropertyService() {
	}

	/**
	 * Returns the activation mode of the given dependency. Returns the STATIC
	 * activation mode if the activation mode attribute is not set.
	 * 
	 * @param dependency
	 * @return the activation mode of the given dependency
	 */
	public static ActivationModes getActivationMode(Dependency dependency) {
		if (!isAttributeSet(dependency, DependencyAttributes.ACTIVATION_MODE.xmlName)) {
			return ActivationModes.STATIC;
		} else {
			String attrString = dependency.getAttribute(DependencyAttributes.ACTIVATION_MODE.xmlName);
			if (attrString.equals(ActivationModes.STATIC.xmlName)) {
				return ActivationModes.STATIC;
			} else if (attrString.equals(ActivationModes.ALTERNATIVE.xmlName)) {
				return ActivationModes.ALTERNATIVE;
			} else {
				throw new IllegalArgumentException("Unknown activation mode for dependency " + dependency.getId());
			}
		}
	}

	public static void setActivationMode(Dependency dependency, ActivationModes activationMode) {
		dependency.setAttribute(DependencyAttributes.ACTIVATION_MODE.xmlName, activationMode.xmlName);
	}
}
