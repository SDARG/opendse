package net.sf.opendse.model;

/**
 * The {@link ApplicationProvider} interface applies to classes that provide an
 * {@link Application}.
 *
 */
public interface ApplicationProvider {

	/**
	 * Returns the {@link Application}.
	 * 
	 * @return the application
	 */
	public <A extends Application<Task, Dependency>> A getApplication();

}
