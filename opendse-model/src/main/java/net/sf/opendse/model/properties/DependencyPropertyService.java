package net.sf.opendse.model.properties;

import net.sf.opendse.model.Dependency;

/**
 * The {@link DependencyPropertyService} offers convenience methods to access
 * the attributes of the {@link Dependency}s.
 * 
 * @author Fedor Smirnov
 *
 */
public class DependencyPropertyService extends AbstractPropertyService {

	public enum DependencyAttributes {
		ROUTING_MODE("routing mode");
		protected String xmlName;

		private DependencyAttributes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	public enum RoutingModes {
		DEFAULT("default"), REDUNDANT("redundant");
		protected String xmlName;

		private RoutingModes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	private DependencyPropertyService() {
	}

	/**
	 * Returns the {@link RoutingModes} of the given {@link Dependency}. Returns the
	 * DEFAULT mode if the corresponding attribute is not set.
	 * 
	 * @param dependency
	 *            the given {@link Dependency}
	 * @return the {@link RoutingModes} of the given {@link Dependency}
	 */
	public static RoutingModes getRoutingMode(Dependency dependency) {
		if (!isAttributeSet(dependency, DependencyAttributes.ROUTING_MODE.xmlName)) {
			return RoutingModes.DEFAULT;
		} else {
			String modeString = (String) getAttribute(dependency, DependencyAttributes.ROUTING_MODE.xmlName);
			if (modeString.equals(RoutingModes.DEFAULT.xmlName)) {
				return RoutingModes.DEFAULT;
			} else if (modeString.equals(RoutingModes.REDUNDANT.xmlName)) {
				return RoutingModes.REDUNDANT;
			} else {
				throw new IllegalArgumentException(
						"Unknown routing mode for dependency " + dependency.getId() + " : " + modeString);
			}
		}
	}

	public static void setRoutingMode(Dependency dependency, RoutingModes routingMode) {
		dependency.setAttribute(DependencyAttributes.ROUTING_MODE.xmlName, routingMode.xmlName);
	}

}
