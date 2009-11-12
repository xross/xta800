package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;

import com.xmos.xta.core.console.BasicConsole;
import com.xmos.xta.core.console.commands.print.StructureDisplayer;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.assertions.FunctionTimeInfo;
import com.xmos.xta.core.engine.assertions.PathTimeInfo;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.references.ReferenceFactory;
import com.xmos.xta.core.engine.references.types.ReferenceTypeFactory.ReferenceClass;

public class RouteAssertions {
	private static final boolean DEBUG_ROUTE_ASSERTIONS = false;
	
	public void applyFunctionAssertion(Engine engine, CommandSource src, 
			Route route, FunctionTimeInfo info, Source source) {
		
		Reference reference = ReferenceFactory.createReference(
				info.getFunction(), ReferenceClass.FUNCTION_REF);	
		
		if (!reference.resolve(engine))
			return;
			
		for (Node node : route.getReferencedFunctionNodes(reference)) {
			route.getRouteUnknowns().setFixedTime(node, info.getTimeUnit(), 
					reference, Type.FUNCTION_TIME, source);	
		}
		route.getRouteUnknowns().invalidateCachedStats();		
	}

	public void applyPathAssertion(Engine engine, CommandSource src, 
			Route route, PathTimeInfo info, Source source) {
		
		Reference fromRef = ReferenceFactory.createReference(
				info.getFrom(), ReferenceClass.ENDPOINT_REF);
		Reference toRef = ReferenceFactory.createReference(
				info.getTo(), ReferenceClass.ENDPOINT_REF);

		if (!fromRef.resolve(engine))
			return;
		if (!toRef.resolve(engine))
			return;
				
		Collection<Node> fromNodes = route.getReferencedNodes(fromRef);
		Collection<Node> toNodes = route.getReferencedNodes(toRef);
		
		RouteAssertionPatterns[] patterns = new RouteAssertionPatterns[] {
				new SameBlockPattern(),
				new DifferentBlockPattern()
		};

		TimeUnit timeUnit = new TimeUnit(route.getRouteDescription().getConfig(), info.getTimeUnit());
		
		for (Node from : fromNodes) {
			assert(from instanceof InstructionNode);
			Node fromBlock = from.getParent();
			assert(fromBlock instanceof BlockNode);
			
			boolean success = false;
			for (Node to : toNodes) {
				assert(to instanceof InstructionNode);
				Node toBlock = to.getParent();
				assert(toBlock instanceof BlockNode);

				for (RouteAssertionPatterns pattern : patterns) {
					if (pattern.match(fromBlock, from, toBlock, to)) {

						if (DEBUG_ROUTE_ASSERTIONS) display("BEFORE", route);						
						pattern.apply(fromRef, toRef, route, fromBlock, from, toBlock, to, timeUnit, source);						
						RouteConstructor.setParent(route.getNode());
						if (DEBUG_ROUTE_ASSERTIONS) display("AFTER", route);
						
						success = true;
						break;
					}
				}
			}

			if (!success) {
				engine.notify(src, Notifier.NOTIFY_WARNING, String.format("failed to apply path assertion: %s, to route: %s", 
						info, route.getRouteDescription().getDescription()));				
			}
			route.getRouteUnknowns().invalidateCachedStats();
			route.getRouteUnknowns().refreshDisplayedUnknowns();
		}
	}	

	private void display(String str, Route route) {
		System.out.printf("------------ %s -------------\n", str);
		StructureDisplayer sd = new StructureDisplayer(new BasicConsole(), 100);	
		Collection<Node> nodes = new ArrayList<Node>();
		nodes.add(route.getNode());
		sd.display(nodes);					
	}
}
