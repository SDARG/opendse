/*******************************************************************************
 * Copyright (c) 2015 OpenDSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package net.sf.opendse.visualization;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import net.sf.opendse.model.Specification;

/**
 * The {@code SpecificationViewer} views a specification in a separate frame.
 * 
 * @author martin.lukasiewycz
 *
 */
public class SpecificationViewer {

	/**
	 * View the {@link Specification}. Exit once the window is closed.
	 * 
	 * @param specification
	 *            the specification to be viewed.
	 */
	public static void view(Specification specification) {
		view(specification, true);
	}

	/**
	 * View the {@link Specification}.
	 * 
	 * @param specification
	 *            the specification to be viewed.
	 * @param existOnClose
	 *            set close operation
	 */
	public static void view(Specification specification, boolean existOnClose) {
		JFrame frame = new JFrame();

		SpecificationPanel panel = new SpecificationPanel(specification);

		frame.setLayout(new BorderLayout());
		frame.add(panel);

		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setTitle("OpenDSE Viewer");
		frame.setVisible(true);

		if (existOnClose) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}
}
