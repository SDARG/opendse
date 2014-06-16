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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.sf.opendse.model.Specification;
import net.sf.opendse.visualization.SpecificationPanel;

import org.opt4j.viewer.ToolBarService;
import org.opt4j.viewer.Viewport;
import org.opt4j.viewer.Widget;
import org.opt4j.viewer.WidgetParameters;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

public class SpecificationToolBarService implements ToolBarService {

	protected final Viewport viewport;
	protected final Provider<SpecificationWidget> provider;
	
	@Inject
	public SpecificationToolBarService(Viewport viewport, Provider<SpecificationWidget> provider) {
		super();
		this.viewport = viewport;
		this.provider = provider;
	}

	@WidgetParameters(title = "Specification")
	@Singleton
	public static class SpecificationWidget implements Widget {

		protected final Specification specification;
		
		@Inject
		public SpecificationWidget(SpecificationWrapper specification){
			this.specification = specification.getSpecification();
		}
		
		@Override
		public JPanel getPanel() {
			SpecificationPanel panel = new SpecificationPanel(specification);
			return panel;
		}

		@Override
		public void init(Viewport viewport) {
		}
	}
	
	@Override
	public JToolBar getToolBar() {
		
		JToolBar toolBar = new JToolBar();
		JButton button = new JButton("specification");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SpecificationWidget widget = provider.get();
				viewport.addWidget(widget);				
			}
		});
		toolBar.add(button);
		
		return toolBar;
	}

}
