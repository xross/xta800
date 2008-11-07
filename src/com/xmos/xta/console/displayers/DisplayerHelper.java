package com.xmos.xta.console.displayers;

import com.xmos.xta.engine.Block;
import com.xmos.xta.engine.Route;

public class DisplayerHelper {
    public static Block getPath(Route route, int pathId) {
        for (Block path : route.completePaths()) {
            Block foundPath = getPath(path, pathId);
            if (foundPath != null) {
                return foundPath;
            }
        }
        return null;        
    }
    
    private static Block getPath(Block path, int pathId) {
        if (pathId == path.getId()) {
            return path;
        }
        for (Block block : path.getPathBlocks()) {
            for (Block loopPath : block.getLoopPaths()) {
                Block foundPath = getPath(loopPath, pathId);
                if (foundPath != null) {
                    block.doneWithLoopPaths();
                    return foundPath;
                }
            }
            block.doneWithLoopPaths();
        }
        return null;
    }
}
