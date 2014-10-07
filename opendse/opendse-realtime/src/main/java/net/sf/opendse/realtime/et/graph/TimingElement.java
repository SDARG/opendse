package net.sf.opendse.realtime.et.graph;

import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class TimingElement extends Mapping<Task, Resource> {

	protected final Task task;
	protected final Resource resource;

	public TimingElement(Task task, Resource resource) {
		super("te-" + task.getId() + "-" + resource.getId(), task, resource);
		this.task = task;
		this.resource = resource;
	}

	public Task getTask() {
		return task;
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimingElement other = (TimingElement) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "" + task + "@" + resource;//+":"+getAttribute("deadline*");
	}

}
