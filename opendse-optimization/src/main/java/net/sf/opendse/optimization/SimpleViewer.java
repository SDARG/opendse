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
package net.sf.opendse.optimization;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.opendse.io.SpecificationWriter;
import net.sf.opendse.model.Specification;
import net.sf.opendse.optimization.io.InputModule;
import net.sf.opendse.visualization.SpecificationViewer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * The {@link SimpleViewer} can be used to display a {@link Specification} given
 * as XML.
 * 
 * @see SpecificationWriter
 * @author Felix Reimann
 *
 */
public class SimpleViewer {
	List<Module> modules = new ArrayList<Module>();

	/**
	 * Creates a new {@link SimpleViewer}.
	 * 
	 * @param filename
	 *            the specification file to view
	 */
	public SimpleViewer(String filename) {
		InputModule inputModule = new InputModule();
		System.out.println(filename);
		inputModule.setFilename(filename);
		addModules(inputModule);
	}

	/**
	 * Additional {@link Module}s to use.
	 * 
	 * @param modules
	 *            the list of modules
	 */
	public void addModules(Module... modules) {
		this.modules.addAll(Arrays.asList(modules));
	}

	/**
	 * View the {@link Specification}.
	 */
	public void view() {
		Injector injector = Guice.createInjector(modules);
		SpecificationViewer.view(injector.getInstance(SpecificationWrapper.class).getSpecification());
	}

	/**
	 * Prints a help message with usage information for the command line.
	 */
	private static void printHelp() {
		System.out.println("OpenDSE Simple Specification Viewer");
		System.out.println();
		System.out.println("Usage: -h | [-m <module>]* <file>");
		System.out.println("-h			Show this help message.");
		System.out.println("-m <module>	Add the given module.");
		System.out.println("<file>		Show file as specification.");
	}

	/**
	 * Instantiates the given {@link Module}s and views the given XML-based
	 * {@link Specification} file or displays a help message.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		if (args.length % 2 == 0 || "-h".equals(args[0])) {
			printHelp();
		} else {
			SimpleViewer simpleViewer = new SimpleViewer(args[args.length - 1]);

			for (int i = 0; i < args.length - 1; i += 2) {
				if ("-m".equals(args[i])) {
					String classname = args[i + 1];
					System.out.println("adding module " + classname);
					Class<?> class1 = null;
					try {
						class1 = Class.forName(classname);
					} catch (ClassNotFoundException e) {
						throw new IllegalArgumentException("module not found: " + classname, e);
					}
					Class<? extends Module> class2 = null;
					try {
						class2 = class1.asSubclass(Module.class);
					} catch (ClassCastException e) {
						throw new IllegalArgumentException("not a module: " + classname, e);
					}
					Module module = null;
					try {
						module = class2.getDeclaredConstructor().newInstance();
					} catch (InstantiationException e) {
						throw new IllegalArgumentException("cannot instantiate module: " + classname, e);
					} catch (IllegalAccessException e) {
						throw new IllegalArgumentException("module inaccessible: " + classname, e);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
					simpleViewer.addModules(module);
				}
			}

			simpleViewer.view();
		}
	}
}
