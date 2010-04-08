package com.xmos.xta.core.engine.analysis.routes;

import java.util.Collection;

import com.xmos.xta.core.engine.AbortedException;
import com.xmos.xta.core.engine.CommandSource;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.Notifier;
import com.xmos.xta.core.engine.analysis.structural.nodes.BlockNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.Unknown.Source;
import com.xmos.xta.core.engine.analysis.structural.nodes.unknowns.UnknownFactory.Type;
import com.xmos.xta.core.engine.defines.FunctionTimeInfo;
import com.xmos.xta.core.engine.defines.PathTimeInfo;
import com.xmos.xta.core.engine.references.Reference;
import com.xmos.xta.core.engine.utils.CoreConfig;
import com.xmos.xta.core.engine.utils.TimeUnit;

public class RouteDefines {
	
	private static RouteDefinePatterns[] m_patterns = new RouteDefinePatterns[] {
			new SameBlockPattern(),
			new DifferentBlockPattern()};

	public static boolean matchPathDefine(Node from, Node to) {
		assert(from instanceof InstructionNode);
		Node fromBlock = from.getParent();
		assert(fromBlock instanceof BlockNode);

		assert(to instanceof InstructionNode);
		Node toBlock = to.getParent();
		assert(toBlock instanceof BlockNode);

		for (RouteDefinePatterns pattern : m_patterns) {
			if (pattern.match(fromBlock, from, toBlock, to)) {
				return true;
			}
		}
		return false;
	}

	public void applyFunctionDefine(CommandSource src, Route route, FunctionTimeInfo info, Source source) throws AbortedException {
		Reference reference = info.getFunction();
		for (Node node : route.getReferencedFunctionNodes(reference)) {
			route.getRouteUnknowns().setFixedTime(node, info.getTimeUnit(),
					reference, Type.FUNCTION_TIME, source);
		}
	}

	public void applyPathDefine(Engine engine, CommandSource src,
			Route route, PathTimeInfo info, Source source) throws AbortedException {

		Reference fromRef = info.getFrom();
		Reference toRef = info.getTo();

		Collection<Node> fromNodes = route.getReferencedNodes(fromRef);
		Collection<Node> toNodes = route.getReferencedNodes(toRef);

		CoreConfig coreConfig = route.getRouteCoreContainer().getCoreConfig();
		TimeUnit timeUnit = new TimeUnit(coreConfig, info.getTimeUnit());

		for (Node from : fromNodes) {
			assert(from instanceof InstructionNode);
			Node fromBlock = from.getParent();
			assert(fromBlock instanceof BlockNode);

			boolean success = false;
			for (Node to : toNodes) {
				assert(to instanceof InstructionNode);
				Node toBlock = to.getParent();
				assert(toBlock instanceof BlockNode);

				for (RouteDefinePatterns pattern : m_patterns) {
					if (pattern.match(fromBlock, from, toBlock, to)) {

						pattern.apply(fromRef, toRef, route, fromBlock, from, toBlock, to, timeUnit, source);
						RouteConstructor.setParent(route.getNode());
					
						success = true;
						break;
					}
				}
			}

			if (!success) {
				engine.notify(src, Notifier.NOTIFY_WARNING, String.format("failed to apply path defines: %s, to route: %s",
						info, route.getRouteCoreContainer().getRouteContainer().getDescription()));
			}
		}

		route.getRouteUnknowns().invalidateCachedStats();
		route.getRouteUnknowns().refreshDisplayedUnknowns();
	}
}
