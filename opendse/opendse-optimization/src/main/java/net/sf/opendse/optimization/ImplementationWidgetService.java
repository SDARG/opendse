/**
 * OpenDSE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OpenDSE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenDSE. If not, see http://www.gnu.org/licenses/.
 */
package net.sf.opendse.optimization;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sf.opendse.model.Specification;
import net.sf.opendse.visualization.SpecificationPanel;

import org.opt4j.core.Individual;
import org.opt4j.viewer.IndividualMouseListener;
import org.opt4j.viewer.Viewport;
import org.opt4j.viewer.Widget;
import org.opt4j.viewer.WidgetParameters;

import com.google.inject.Inject;

public class ImplementationWidgetService implements IndividualMouseListener {

	protected final Viewport viewport;

	@Inject
	public ImplementationWidgetService(Viewport viewport) {
		super();
		this.viewport = viewport;
	}

	@WidgetParameters(title = "Implementation")
	class ImplementationWidget implements Widget {

		protected final Specification implementation;
		
		public ImplementationWidget(Specification implementation){
			this.implementation = implementation;
		}
		
		@Override
		public JPanel getPanel() {
			SpecificationPanel panel = new SpecificationPanel(implementation);
			return panel;
		}

		@Override
		public void init(Viewport viewport) {
		}
	}

	@Override
	public void onDoubleClick(Individual individual, Component component, Point p) {
		ImplementationWrapper wrapper = (ImplementationWrapper)individual.getPhenotype();
		Widget widget = new ImplementationWidget(wrapper.getImplementation());
		viewport.addWidget(widget);
	}

	@Override
	public void onPopup(Individual individual, Component component, Point p, JPopupMenu menu) {

	}

}
