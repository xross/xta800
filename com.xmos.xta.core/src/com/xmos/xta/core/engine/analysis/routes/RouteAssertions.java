package com.xmos.xta.core.engine.analysis.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.TimeUnit;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.references.Reference;

public class RouteAssertions {
	public void apply(Notifier notifer, CommandSource src, List<Route> assertionRoutes, List<Route> routes) {
		for (Route assertionRoute : assertionRoutes) {
			RouteDescription routeDescription = assertionRoute.getRouteDescription();			
			
			if (routeDescription.isAssertion()) {				
				TimeUnit assertedTime = assertionRoute.getRouteTiming().getMaxTime();

				for (Route route : routes) {					
					switch (routeDescription.getMode()) {
					case FUNCTION:
						applyFunctionAssertion(notifer, src, assertionRoute, route, assertedTime);
						break;
						
					case PATH:
						applyPathAssertion(notifer, src, assertionRoute, route, assertedTime);
						break;
					}					
				}						
			}
		}		
	}
	
	private void applyFunctionAssertion(Notifier notifer, CommandSource src, 
			Route assertionRoute, Route route, TimeUnit assertedTime) {
		
		Reference reference = assertionRoute.getRouteDescription().getFunctionReference();
		for (Node node : route.getReferencedFunctionNodes(reference)) {
			route.getRouteUnknowns().setFixedTime(node, assertedTime);							
		}		
	}
	
	private void applyPathAssertion(Notifier notifer, CommandSource src, 
			Route assertionRoute, Route route, TimeUnit assertedTime) {
		
		// Finds the block that contains the instruction start node
		Collection<Node> fromNodes = route.getReferencedNodes(
				assertionRoute.getRouteDescription().getStartPoint());

		// Finds the block that contains the instruction end node
		Collection<Node> toNodes = new ArrayList<Node>();
		for (Reference to : assertionRoute.getRouteDescription().getEndPoint()) {
			toNodes.addAll(route.getReferencedNodes(to));
		}
		
		RouteAssertionPatterns[] patterns = new RouteAssertionPatterns[] {
				new SameBlockPattern(),
				new DifferentBlockPattern()
		};

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
						pattern.apply(route, fromBlock, from, toBlock, to, assertedTime);
						success = true;
						break;
					}
				}
			}
			
			if (!success) {
				notifer.notify(src, Notifier.NOTIFY_WARNING, String.format("failed to apply assertion route: %s", 
						assertionRoute.getRouteDescription().getDescription()));				
			}
		}
	}	
}
