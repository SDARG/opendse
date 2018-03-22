package net.sf.opendse.encoding.mapping;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.M;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class DesignerMappingsConstraintGeneratorTest {

	@Test(expected = IllegalArgumentException.class)
	public void testMissingMapping() {
		Task task = new Task("task");
		T tVar = Variables.var(task);
		Set<T> processVars = new HashSet<T>();
		processVars.add(tVar);
		DesignerMappingsConstraintGenerator generator = new DesignerMappingsConstraintGenerator();
		generator.toConstraints(processVars, new Mappings<Task, Resource>());
	}

	@Test
	public void test() {
		Task task = new Task("task");
		Resource res1 = new Resource("res1");
		Resource res2 = new Resource("res2");
		Mapping<Task, Resource> m1 = new Mapping<Task, Resource>("m1", task, res1);
		Mapping<Task, Resource> m2 = new Mapping<Task, Resource>("m2", task, res2);
		Mappings<Task, Resource> mappings = new Mappings<Task, Resource>();
		mappings.add(m1);
		mappings.add(m2);
		T tVar = Variables.var(task);
		M mVar1 = Variables.var(m1);
		M mVar2 = Variables.var(m2);
		Set<T> applVars = new HashSet<T>();
		applVars.add(tVar);
		DesignerMappingsConstraintGenerator generator = new DesignerMappingsConstraintGenerator();
		Set<Constraint> constraints = generator.toConstraints(applVars, mappings);
		assertEquals(1, constraints.size());

		Set<Object> taskActivated = new HashSet<Object>();
		taskActivated.add(tVar);
		Set<Object> mappingDeactivation = new HashSet<Object>();
		mappingDeactivation.add(mVar1);

		ConstraintVerifier taskActive = new ConstraintVerifier(taskActivated, mappingDeactivation, constraints);
		taskActive.verifyVariableActivated(mVar2);

		Set<Object> taskAndMappingDeactivated = new HashSet<Object>();
		taskAndMappingDeactivated.add(tVar);
		taskAndMappingDeactivated.add(mVar1);

		ConstraintVerifier taskInActive = new ConstraintVerifier(new HashSet<Object>(), taskAndMappingDeactivated,
				constraints);
		taskInActive.verifyVariableDeactivated(mVar2);

		taskActivated.add(mVar1);
		ConstraintVerifier mappingAndTaskActive = new ConstraintVerifier(taskActivated, new HashSet<Object>(),
				constraints);
		mappingAndTaskActive.verifyVariableDeactivated(mVar2);
	}
}
