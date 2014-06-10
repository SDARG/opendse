package net.sf.opendse.optimization.test.generator;

/**
 * The {@code IdProvider} generates unique ids with a given prefix.
 * 
 * @author lukasiewycz
 * 
 */
public class IdProvider {

	protected final String s;
	protected int i = 0;

	/**
	 * Constructs an {@code IdProvider}.
	 * 
	 * @param prefix
	 *            the prefix
	 */
	public IdProvider(String prefix) {
		this.s = prefix;
	}

	/**
	 * Returns the next id.
	 * 
	 * @return the next id
	 */
	public synchronized String next() {
		String result = s + i;
		i++;
		return result;
	}

}
