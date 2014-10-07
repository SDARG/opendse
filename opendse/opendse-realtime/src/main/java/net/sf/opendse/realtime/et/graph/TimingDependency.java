package net.sf.opendse.realtime.et.graph;

public abstract class TimingDependency {
	
	protected static int idCounter = 0;
	
	protected final int id;
	
	public TimingDependency(){
		id = idCounter++;
	}
	
	

	public int getId() {
		return id;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		TimingDependency other = (TimingDependency) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimingDependency [id=" + id + "]";
	}
	
	

}
