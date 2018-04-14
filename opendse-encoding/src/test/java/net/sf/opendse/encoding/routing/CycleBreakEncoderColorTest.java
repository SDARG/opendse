package net.sf.opendse.encoding.routing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opt4j.satdecoding.Constraint;

import edu.uci.ics.jung.graph.util.EdgeType;
import net.sf.opendse.encoding.variables.CLRR;
import net.sf.opendse.encoding.variables.ColoredCommNode;
import net.sf.opendse.encoding.variables.T;
import net.sf.opendse.encoding.variables.Variables;
import net.sf.opendse.model.Architecture;
import net.sf.opendse.model.Communication;
import net.sf.opendse.model.Link;
import net.sf.opendse.model.Models.DirectedLink;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import verification.ConstraintVerifier;

public class CycleBreakEncoderColorTest {

	@Test
	public void testPaintNeighborhoodDifferently() {
		Communication comm = new Communication("comm");
		Resource res = new Resource("res");
		Resource predecessor = new Resource("predecessor");
		Resource successor = new Resource("successor");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		DirectedLink inLink = new DirectedLink(l1, predecessor, res);
		DirectedLink outLink = new DirectedLink(l2, res, successor);
		CycleBreakEncoderColor breaker = new CycleBreakEncoderColor();
		Set<Constraint> cs = breaker.paintNeighborhoodDifferently(comm, inLink, outLink, predecessor, successor);
		CLRR inLinkUsed = Variables.varCLRR(comm, inLink);
		CLRR outLinkUsed = Variables.varCLRR(comm, outLink);
		ColoredCommNode preRed = Variables.varColoredCommNode(comm, predecessor, "red");
		ColoredCommNode sucRed = Variables.varColoredCommNode(comm, successor, "red");
		ColoredCommNode sucBlue = Variables.varColoredCommNode(comm, successor, "blue");
		ConstraintVerifier oneLinkUnused = new ConstraintVerifier(cs);
		oneLinkUnused.deactivateVariable(inLinkUsed);
		oneLinkUnused.activateVariable(preRed);
		oneLinkUnused.verifyVariableNotFixed(sucRed);
		oneLinkUnused.verifyVariableNotFixed(sucBlue);
		ConstraintVerifier bothLinksUsed = new ConstraintVerifier(cs);
		bothLinksUsed.activateVariable(inLinkUsed);
		bothLinksUsed.activateVariable(outLinkUsed);
		bothLinksUsed.activateVariable(preRed);
		bothLinksUsed.verifyVariableDeactivated(sucRed);
		bothLinksUsed.verifyVariableNotFixed(sucBlue);
	}

	@Test
	public void testPaintNeighborsDifferently() {
		Communication comm = new Communication("comm");
		Resource first = new Resource("first");
		Resource second = new Resource("second");
		Link l = new Link("l");
		DirectedLink dLink = new DirectedLink(l, first, second);
		CycleBreakEncoderColor breaker = new CycleBreakEncoderColor();
		Set<Constraint> cs = breaker.paintNeighborsDifferently(comm, dLink, first, second);
		assertEquals(3, cs.size());
		CLRR linkUsed = Variables.varCLRR(comm, dLink);
		ColoredCommNode firstRed = Variables.varColoredCommNode(comm, first, "red");
		ColoredCommNode secondRed = Variables.varColoredCommNode(comm, second, "red");
		ColoredCommNode secondBlue = Variables.varColoredCommNode(comm, second, "blue");
		ConstraintVerifier linkNotUsed = new ConstraintVerifier(cs);
		linkNotUsed.deactivateVariable(linkUsed);
		linkNotUsed.activateVariable(firstRed);
		linkNotUsed.verifyVariableNotFixed(secondRed);
		linkNotUsed.verifyVariableNotFixed(secondBlue);
		ConstraintVerifier linkActive = new ConstraintVerifier(cs);
		linkActive.activateVariable(linkUsed);
		linkActive.activateVariable(firstRed);
		linkActive.verifyVariableDeactivated(secondRed);
		linkActive.verifyVariableNotFixed(secondBlue);
	}

	@Test
	public void testPaint3Colors() {
		Resource res = new Resource("res");
		Task task = new Task("task");
		CycleBreakEncoderColor breaker = new CycleBreakEncoderColor();
		Set<Constraint> cs = new HashSet<Constraint>();
		cs.add(breaker.paintResource3Colors(task, res));
		ConstraintVerifier verifyPainting = new ConstraintVerifier(cs);
		ColoredCommNode resRed = Variables.varColoredCommNode(task, res, "red");
		ColoredCommNode resBlue = Variables.varColoredCommNode(task, res, "blue");
		verifyPainting.verifyVariableNotFixed(resBlue);
		verifyPainting.verifyVariableNotFixed(resRed);
		verifyPainting.activateVariable(resBlue);
		verifyPainting.verifyVariableDeactivated(resRed);
	}

	@Test
	public void testTwoColoring() {
		Communication comm = new Communication("comm");
		T commVar = Variables.varT(comm);
		Resource src = new Resource("src");
		Resource dest = new Resource("dest");
		Link l = new Link("l");
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		routing.addEdge(l, src, dest, EdgeType.UNDIRECTED);
		CycleBreakEncoderColor breaker = new CycleBreakEncoderColor();
		Set<Constraint> cs = breaker.performTwoColoring(commVar, routing);
		CLRR linkUsed = Variables.varCLRR(comm, l, src, dest);
		ColoredCommNode srcBlack = Variables.varColoredCommNode(comm, src, "black");
		ColoredCommNode destBlack = Variables.varColoredCommNode(comm, dest, "black");
		// verify deactivated link
		ConstraintVerifier verifyUnactiveL1 = new ConstraintVerifier(cs);
		verifyUnactiveL1.deactivateVariable(linkUsed);
		verifyUnactiveL1.deactivateVariable(srcBlack);
		verifyUnactiveL1.verifyVariableNotFixed(destBlack);
		ConstraintVerifier verifyUnactiveL2 = new ConstraintVerifier(cs);
		verifyUnactiveL2.deactivateVariable(linkUsed);
		verifyUnactiveL2.activateVariable(srcBlack);
		verifyUnactiveL2.verifyVariableNotFixed(destBlack);
		// verify link activation possibility
		ConstraintVerifier verifyL = new ConstraintVerifier(cs);
		verifyL.activateVariable(srcBlack);
		verifyL.deactivateVariable(destBlack);
		verifyL.verifyVariableNotFixed(linkUsed);
		ConstraintVerifier verifyL2 = new ConstraintVerifier(cs);
		verifyL2.deactivateVariable(srcBlack);
		verifyL2.activateVariable(destBlack);
		verifyL2.verifyVariableNotFixed(linkUsed);
		ConstraintVerifier verifyL3 = new ConstraintVerifier(cs);
		verifyL3.activateVariable(srcBlack);
		verifyL3.activateVariable(destBlack);
		verifyL3.verifyVariableDeactivated(linkUsed);
		ConstraintVerifier verifyL4 = new ConstraintVerifier(cs);
		verifyL4.deactivateVariable(srcBlack);
		verifyL4.deactivateVariable(destBlack);
		verifyL4.verifyVariableDeactivated(linkUsed);
	}

	@Test
	public void testEvenCycle() {
		Communication comm = new Communication("comm");
		T commVar = Variables.varT(comm);
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Resource r3 = new Resource("r3");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		Link l3 = new Link("l3");
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r2, r3, EdgeType.UNDIRECTED);
		routing.addEdge(l3, r3, r0, EdgeType.UNDIRECTED);
		CycleBreakEncoderColor cycleBreaker = new CycleBreakEncoderColor();
		Set<Constraint> cs = cycleBreaker.toConstraints(commVar, routing);
		Set<Object> activated = new HashSet<Object>();
		activated.add(Variables.varCLRR(comm, l0, r0, r1));
		activated.add(Variables.varCLRR(comm, l1, r1, r2));
		activated.add(Variables.varCLRR(comm, l2, r2, r3));
		activated.add(Variables.varCLRR(comm, l3, r3, r0));
		boolean assertionError = false;
		try {
			new ConstraintVerifier(activated, new HashSet<Object>(), cs);
		} catch (AssertionError error) {
			assertionError = true;
		}
		assertTrue(assertionError);
	}

	@Test
	public void testOddCycle() {
		Communication comm = new Communication("comm");
		T commVar = Variables.varT(comm);
		Architecture<Resource, Link> routing = new Architecture<Resource, Link>();
		Resource r0 = new Resource("r0");
		Resource r1 = new Resource("r1");
		Resource r2 = new Resource("r2");
		Link l0 = new Link("l0");
		Link l1 = new Link("l1");
		Link l2 = new Link("l2");
		routing.addEdge(l0, r0, r1, EdgeType.UNDIRECTED);
		routing.addEdge(l1, r1, r2, EdgeType.UNDIRECTED);
		routing.addEdge(l2, r2, r0, EdgeType.UNDIRECTED);
		CycleBreakEncoderColor cycleBreaker = new CycleBreakEncoderColor();
		Set<Constraint> cs = cycleBreaker.toConstraints(commVar, routing);
		Set<Object> activated = new HashSet<Object>();
		activated.add(Variables.varCLRR(comm, l0, r0, r1));
		activated.add(Variables.varCLRR(comm, l1, r1, r2));
		activated.add(Variables.varCLRR(comm, l2, r2, r0));
		boolean assertionError = false;
		try {
			new ConstraintVerifier(activated, new HashSet<Object>(), cs);
		} catch (AssertionError error) {
			assertionError = true;
		}
		assertTrue(assertionError);
	}
}
