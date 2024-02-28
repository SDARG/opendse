package net.sf.opendse.optimization;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opt4j.core.Individual;
import org.opt4j.core.Objectives;
import org.opt4j.core.optimizer.Population;

public class StagnationRestartTest {

	class MockIndividual extends Individual {
		public MockIndividual(boolean dominated) {
			super();
			setObjectives(dominated ? new MockDominatedObjectives() : new MockDominantObjectives());
		}
	}

	class MockPopulation extends Population {

	}

	class MockDominatedObjectives extends Objectives {
		@Override
		public boolean dominates(Objectives opponent) {
			return false;
		}
	}

	class MockDominantObjectives extends Objectives {
		@Override
		public boolean dominates(Objectives opponent) {
			return true;
		}
	}

	@Test
	public void testNoStagnation() {
		Population population = new Population();
		Individual indi1 = new MockIndividual(false);
		Individual indi2 = new MockIndividual(false);
		population.add(indi1);
		population.add(indi2);
		StagnationRestart restart = new StagnationRestart(population, 20);
		restart.archive.add(indi1);
		restart.archive.add(indi2);
		Assertions.assertEquals(0, restart.lastUpdate);
		restart.iterationComplete(21);
		Assertions.assertEquals(21, restart.lastUpdate);
		Assertions.assertFalse(population.isEmpty());
		Assertions.assertFalse(restart.archive.isEmpty());
	}

	@Test
	public void testStagnation() {
		Population population = new Population();
		Individual indi1 = new MockIndividual(true);
		Individual indi2 = new MockIndividual(true);
		population.add(indi1);
		population.add(indi2);
		StagnationRestart restart = new StagnationRestart(population, 20);
		restart.archive.add(indi1);
		restart.archive.add(indi2);
		Assertions.assertEquals(0, restart.lastUpdate);
		restart.iterationComplete(5);
		Assertions.assertEquals(0, restart.lastUpdate);
		Assertions.assertFalse(population.isEmpty());
		Assertions.assertFalse(restart.archive.isEmpty());
		restart.iterationComplete(21);
		Assertions.assertEquals(21, restart.lastUpdate);
		Assertions.assertTrue(population.isEmpty());
		Assertions.assertTrue(restart.archive.isEmpty());
	}

}
