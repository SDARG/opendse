package net.sf.opendse.optimization;


import org.opt4j.core.common.random.Rand;
import org.opt4j.core.optimizer.Control;
import org.opt4j.satdecoding.SATManager;

import net.sf.opendse.optimization.encoding.Interpreter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SATCreatorDecoderTest {

	protected SATCreatorDecoder getObject() {
		VariableClassOrder order = mock(VariableClassOrder.class);
		SATManager manager = mock(SATManager.class);
		Rand random = mock(Rand.class);
		SATConstraints constraints = mock(SATConstraints.class);
		SpecificationWrapper specificationWrapper = mock(SpecificationWrapper.class);
		Interpreter interpreter = mock(Interpreter.class);
		Control control = mock(Control.class);
		SATCreatorDecoder result = new SATCreatorDecoder(order, manager, random, constraints, specificationWrapper, interpreter, control, true);
		return result;
	}
	
	@Test
	public void testBoundsDefault() {
		// default case: only object is in the list
		int orderSize = 1;
		int orderIndex = 0;
		SATCreatorDecoder testObject = getObject();
		Assertions.assertEquals(0.0, testObject.getLowerOrderBound(orderSize, orderIndex), 0.0);
		Assertions.assertEquals(1.0, testObject.getUpperOrderBound(orderSize, orderIndex), 0.0);
	}
	
	@Test
	public void testBoundsFiveEntries() {
		// five entries in the list
		int orderSize = 5;
		SATCreatorDecoder testObject = getObject();
		Assertions.assertEquals(0.8, testObject.getLowerOrderBound(orderSize, 0), 0.0);
		Assertions.assertEquals(1.0, testObject.getUpperOrderBound(orderSize, 0), 0.0);
		
		Assertions.assertEquals(0.6, testObject.getLowerOrderBound(orderSize, 1), 0.0);
		Assertions.assertEquals(0.8, testObject.getUpperOrderBound(orderSize, 1), 0.0);
		
		Assertions.assertEquals(0.4, testObject.getLowerOrderBound(orderSize, 2), 0.0);
		Assertions.assertEquals(0.6, testObject.getUpperOrderBound(orderSize, 2), 0.0);
		
		Assertions.assertEquals(0.2, testObject.getLowerOrderBound(orderSize, 3), 0.0);
		Assertions.assertEquals(0.4, testObject.getUpperOrderBound(orderSize, 3), 0.0);
		
		Assertions.assertEquals(0.0, testObject.getLowerOrderBound(orderSize, 4), 0.0);
		Assertions.assertEquals(0.2, testObject.getUpperOrderBound(orderSize, 4), 0.0);
	}
	
	@Test
	public void testIgnoredVariable1() {
		assertThrows(IllegalArgumentException.class, () -> {
			// five entries, but variable ignored
			int orderSize = 5;
			int orderIndex = -1;
			SATCreatorDecoder testObject = getObject();
			testObject.getLowerOrderBound(orderSize, orderIndex);
		});
	}
	
	@Test
	public void testIgnoredVariable2() {
		assertThrows(IllegalArgumentException.class, () -> {
			// five entries, but variable ignored
			int orderSize = 5;
			int orderIndex = -1;
			SATCreatorDecoder testObject = getObject();
			testObject.getUpperOrderBound(orderSize, orderIndex);
		});
	}
}
