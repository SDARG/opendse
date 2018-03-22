package net.sf.opendse.encoding.mapping;

import static org.junit.Assert.*;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import net.sf.opendse.encoding.variables.ApplicationVariable;
import net.sf.opendse.encoding.variables.DTT;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Dependency;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import net.sf.opendse.model.properties.ProcessPropertyService;
import net.sf.opendse.model.properties.ProcessPropertyService.MappingModes;

import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MappingModeEncodingTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testToConstraints() {
		Task t1 = new Task("t1");
		Communication comm = new Communication("comm");
		Task t2 = new Task("t2");
		Dependency d1 = new Dependency("d1");
		Dependency d2 = new Dependency("d2");
		T tvar1 = Variables.var(t1);
		T tvar2 = Variables.var(t2);
		T tVarComm = Variables.var(comm);
		DTT dttVar1 = Variables.var(d1, t1, comm);
		DTT dttVar2 = Variables.var(d2, comm, t2);
		Set<ApplicationVariable> applVars = new HashSet<ApplicationVariable>();
		applVars.add(tvar1);
		applVars.add(tvar2);
		applVars.add(tVarComm);
		applVars.add(dttVar1);
		applVars.add(dttVar2);
		Set<T> processVariables = new HashSet<T>();
		processVariables.add(tvar1);
		processVariables.add(tvar2);
		MappingConstraintGeneratorManager generatorManager = mock(MappingConstraintGeneratorManager.class);
		MappingConstraintGenerator constraintGenerator = mock(MappingConstraintGenerator.class);
		when(generatorManager.getMappingConstraintGenerator(MappingModes.DESIGNER)).thenReturn(constraintGenerator);
		when(constraintGenerator.toConstraints(anySet(), any(Mappings.class)))
				.thenReturn(new HashSet<Constraint>());
		MappingModeEncoding encoding = new MappingModeEncoding(generatorManager);
		Set<Constraint> mappingConstraints = encoding.toConstraints(new Mappings<Task, Resource>(), applVars);
		assertTrue(mappingConstraints.isEmpty());
		verify(generatorManager).getMappingConstraintGenerator(MappingModes.DESIGNER);
		verify(constraintGenerator).toConstraints(anySet(), any(Mappings.class));
	}

	@Test
	public void testFilterProcessVariables() {
		Task t1 = new Task("t1");
		T tVar1 = Variables.var(t1);
		Task t2 = new Task("t2");
		T tVar2 = Variables.var(t2);
		Task t3 = new Task("t3");
		T tVar3 = Variables.var(t3);
		Set<T> processVars = new HashSet<T>();
		processVars.add(tVar1);
		processVars.add(tVar2);
		processVars.add(tVar3);
		ProcessPropertyService.setMappingMode(t3, MappingModes.TYPE);
		MappingConstraintGeneratorManager generatorManager = mock(MappingConstraintGeneratorManager.class);
		MappingModeEncoding encoding = new MappingModeEncoding(generatorManager);
		Map<MappingModes, Set<T>> result = encoding.filterProcessVariables(processVars);
		assertTrue(result.containsKey(MappingModes.DESIGNER));
		assertTrue(result.containsKey(MappingModes.TYPE));
		Set<T> designerProcesses = result.get(MappingModes.DESIGNER);
		Set<T> typeProcesses = result.get(MappingModes.TYPE);
		assertEquals(2, designerProcesses.size());
		assertTrue(designerProcesses.contains(tVar1));
		assertTrue(designerProcesses.contains(tVar2));
		assertEquals(1, typeProcesses.size());
		assertTrue(typeProcesses.contains(tVar3));
	}
}
