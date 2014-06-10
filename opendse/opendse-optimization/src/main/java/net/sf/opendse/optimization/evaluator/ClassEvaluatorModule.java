package net.sf.opendse.optimization.evaluator;

import net.sf.opendse.optimization.ImplementationEvaluator;

import org.opt4j.core.config.annotations.Multi;

@Multi
public class ClassEvaluatorModule extends EvaluatorModule {

	protected String classname = "";
	protected String config = "";

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Override
	protected void config() {

		try {
			Class<? extends ImplementationEvaluator> clazz = Class.forName(classname).asSubclass(
					ImplementationEvaluator.class);
			bindEvaluator(clazz);

			bindConstant("config", clazz).to(config);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
