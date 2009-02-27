package com.xmos.xta.engine;

public interface RouteStart {

	void addCompletePath(PathBasicBlock path);

	void addUnresolvedPath(PathBasicBlock path);

}
