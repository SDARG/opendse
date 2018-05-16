package net.sf.opendse.model.properties;

import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Element;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Resource;

/**
 * The {@link ArchitectureElementPropertyService} offers convenience methods to
 * access the properties of the {@link Element}s of the {@link Architecture}.
 * 
 * @author Fedor Smirnov
 *
 */
public class ArchitectureElementPropertyService extends AbstractPropertyService {

	public enum ArchitectureElementAttributes {
		ROUTING_VARIETY("offers routing variaty");
		protected String xmlName;

		private ArchitectureElementAttributes(String xmlName) {
			this.xmlName = xmlName;
		}
	}

	/**
	 * Checks whether the given {@link Element} is an architecture element. Throws
	 * an {@link IllegalArgumentException} is this is not the case.
	 * 
	 * @param element
	 *            the input {@link Element}
	 */
	protected static void checkElement(Element element) {
		if (!(element instanceof Resource || element instanceof Link)) {
			throw new IllegalArgumentException("The element " + element.getId() + " is not an architecture element.");
		}
	}

	/**
	 * Sets the attribute that dictates whether the given element should be
	 * considered for the encoding of the routings.
	 * 
	 * @param element
	 * @param offersRoutingVariaty
	 */
	public static void setOfferRoutingVariety(Element element, boolean offersRoutingVariaty) {
		checkElement(element);
		String attrName = ArchitectureElementAttributes.ROUTING_VARIETY.xmlName;
		element.setAttribute(attrName, offersRoutingVariaty);
	}

	/**
	 * Returns {@code true} if the {@link Element} has to be considered during the
	 * encoding of the routings.
	 * 
	 * @param element
	 *            the given {@link Element}
	 * @return {@code true} if the {@link Element} has to be considered during the
	 *         encoding of the routings
	 */
	public static boolean getOffersRoutingVariety(Element element) {
		checkElement(element);
		String attrName = ArchitectureElementAttributes.ROUTING_VARIETY.xmlName;
		if (!isAttributeSet(element, attrName)) {
			return true;
		} else {
			return (Boolean) getAttribute(element, attrName);
		}
	}
}
