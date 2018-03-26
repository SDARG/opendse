package net.sf.opendse.model.properties;

import net.sf.opendse.model.Task;

/**
 * The {@link ProcessPropertyService} offers convenience methods to access the
 * attributes of processes.
 * 
 * @author Fedor Smirnov
 *
 */
public class ProcessPropertyService extends AbstractPropertyService {

	public enum ProcessAttributes {
		MAPPING_MODE("mapping mode");
		protected String xmlName;

		private ProcessAttributes(String xmlName) {
			this.xmlName = xmlName;
		}
	}
	
	public enum MappingModes {
		DESIGNER("designer"), TYPE("type");
		protected String xmlName;

		private MappingModes(String xmlName) {
			this.xmlName = xmlName;
		}
	}
	
	private ProcessPropertyService() {
	}
	
	/**
	 * Returns the mapping mode of the given process. The DESIGNER mode is assumed if the attribute is not set.
	 * 
	 * @param task
	 * @return the mapping mode of the given process (DESIGNER mode as default)
	 */
	public static MappingModes getMappingMode(Task task) {
		checkTask(task);
		if (!isAttributeSet(task, ProcessAttributes.MAPPING_MODE.xmlName)) {
			return MappingModes.DESIGNER;
		} else {
			String attrString = task.getAttribute(ProcessAttributes.MAPPING_MODE.xmlName);
			if (attrString.equals(MappingModes.DESIGNER.xmlName)) {
				return MappingModes.DESIGNER;
			} else if (attrString.equals(MappingModes.TYPE.xmlName)) {
				return MappingModes.TYPE;
			} else {
				throw new IllegalArgumentException("Unknown mapping mode for process " + task.getId());
			}
		}
	}
	
	public static void setMappingMode(Task task, MappingModes mappingMode) {
		checkTask(task);
		task.setAttribute(ProcessAttributes.MAPPING_MODE.xmlName, mappingMode.xmlName);
	}
	
	protected static void checkTask(Task task) {
		if (!TaskPropertyService.isProcess(task)) {
			throw new IllegalArgumentException("Task " + task.getId() + " is not a process");
		}
	}
}
