package net.sf.opendse.encoding.module;

import org.opt4j.core.config.Icons;
import org.opt4j.core.config.annotations.Icon;
import org.opt4j.core.config.annotations.Name;
import org.opt4j.core.config.annotations.Parent;
import org.opt4j.core.start.Opt4JModule;

import net.sf.opendse.encoding.interpreter.SpecificationPostProcessorProxy;
import net.sf.opendse.encoding.preprocessing.ProxySearch;
import net.sf.opendse.encoding.preprocessing.ProxySearchReduction;
import net.sf.opendse.encoding.routing.ProxyEncoder;
import net.sf.opendse.encoding.routing.ProxyEncoderLazy;
import net.sf.opendse.optimization.DesignSpaceExplorationModule;

/**
 * The {@link ProxyRoutingModule} binds the classes necessary for a proxy-based
 * routing encoding.
 * 
 * @author Fedor Smirnov
 *
 */
@Parent(DesignSpaceExplorationModule.class)
@Icon(Icons.PROBLEM)
public class ProxyRoutingModule extends Opt4JModule {

	@Name("activate the elements in the proxy areas")
	protected boolean activateProxyAreas = true;

	public boolean isActivateProxyAreas() {
		return activateProxyAreas;
	}

	public void setActivateProxyAreas(boolean activateProxyAreas) {
		this.activateProxyAreas = activateProxyAreas;
	}

	@Override
	protected void config() {
		if (!activateProxyAreas) {
			bind(ProxyEncoder.class).to(ProxyEncoderLazy.class);
			bind(ProxySearchReduction.class).asEagerSingleton();
			bind(SpecificationPostProcessorProxy.class).asEagerSingleton();
		} else {
			bind(ProxySearch.class).asEagerSingleton();
		}
	}
}
