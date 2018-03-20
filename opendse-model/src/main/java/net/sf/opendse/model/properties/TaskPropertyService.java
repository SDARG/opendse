package net.sf.opendse.model.properties;

import net.sf.opendse.model.ICommunication;
import net.sf.opendse.model.Task;

/**
 * offers convenience methods to access properties of tasks
 * 
 * @author Fedor Smirnov
 *
 */
public class TaskPropertyService extends AbstractPropertyService {

	public enum TaskAttributes {
		ACTIVATION_MODE("activation mode");
		protected String xmlName;

		private TaskAttributes(String xmlName) {
			this.xmlName = xmlName;
		}

		public String getXmlName() {
			return this.xmlName;
		}
	}

	public enum ActivationModes {
		STATIC("static"), ALTERNATIVE("alternative");
		protected String xmlName;

		private ActivationModes(String xmlName) {
			this.xmlName = xmlName;
		}

		public String getXmlName() {
			return this.xmlName;
		}
	}

	private TaskPropertyService() {
	}

	/**
	 * Returns the activation mode of the given task. Returns the STATIC activation
	 * mode if the attribute is not set.
	 * 
	 * @param task
	 * @return the activation mode of the task
	 */
	public static ActivationModes getActivationMode(Task task) {
		if (!isAttributeSet(task, TaskAttributes.ACTIVATION_MODE.xmlName)) {
			return ActivationModes.STATIC;
		} else {
			String attrString = task.getAttribute(TaskAttributes.ACTIVATION_MODE.xmlName);
			if (attrString.equals(ActivationModes.STATIC.xmlName)) {
				return ActivationModes.STATIC;
			} else if (attrString.equals(ActivationModes.ALTERNATIVE.xmlName)) {
				return ActivationModes.ALTERNATIVE;
			} else {
				throw new IllegalArgumentException("Unknown activation mode for task " + task.getId());
			}
		}
	}

	public static void setActivationMode(Task task, ActivationModes activationMode) {
		task.setAttribute(TaskAttributes.ACTIVATION_MODE.xmlName, activationMode.getXmlName());
	}

	public static boolean isProcess(Task task) {
		return !isCommunication(task);
	}

	public static boolean isCommunication(Task task) {
		return task instanceof ICommunication;
	}

}
