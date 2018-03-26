package net.sf.opendse.model.properties;

import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Task;

/**
 * The {@link CommunicationPropertyService} offers convenience methods to access
 * the attributes of {@link Communication} tasks.
 * 
 * @author Fedor Smirnov
 *
 */
public class CommunicationPropertyService extends AbstractPropertyService {

	public enum CommunicationAttributes {
		ROUTING_MODE("mapping mode");
		protected String xmlName;

		private CommunicationAttributes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	public enum RoutingModes {
		DEFAULT("default"), CHIP("chip");
		protected String xmlName;

		private RoutingModes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	private CommunicationPropertyService() {
	}

	protected static void checkTask(Task comm) {
		if (!TaskPropertyService.isCommunication(comm)) {
			throw new IllegalArgumentException("The task " + comm.getId() + " is not a communication.");
		}
	}

	/**
	 * Returns the {@link RoutingModes} of the given {@link Task}. If the attribute
	 * is not set, the DEFAULT routing mode is returned.
	 * 
	 * @param comm
	 *            the communication task
	 * @return the {@link RoutingModes} of the communication task
	 */
	public static RoutingModes getRoutingMode(Task comm) {
		checkTask(comm);
		if (!isAttributeSet(comm, CommunicationAttributes.ROUTING_MODE.xmlName)) {
			return RoutingModes.DEFAULT;
		} else {
			String modeString = (String) getAttribute(comm, CommunicationAttributes.ROUTING_MODE.xmlName);
			if (modeString.equals(RoutingModes.DEFAULT.xmlName)) {
				return RoutingModes.DEFAULT;
			} else if (modeString.equals(RoutingModes.CHIP.xmlName)) {
				return RoutingModes.CHIP;
			} else {
				throw new IllegalArgumentException(
						"Unknown routing mode for communication " + comm.getId() + " :" + modeString);
			}
		}
	}

	public static void setRoutingMode(Task comm, RoutingModes routingMode) {
		checkTask(comm);
		comm.setAttribute(CommunicationAttributes.ROUTING_MODE.xmlName, routingMode.xmlName);
	}
}
