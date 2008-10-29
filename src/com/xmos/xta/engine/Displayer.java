package com.xmos.xta.engine;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;

public abstract class Displayer {
    public abstract void displayRoute(Route route, 
            DebugInfo debugInfo, MemoryImage memoryImage, SymbolTable m_symbolTable);
    
    public abstract void notifyLoadExecutable();
    public abstract void notifyAnalysePaths();
    public abstract void notifyUpdateState();

    public Block getPath(Route route, int pathId) {
        for (Block path : route.completePaths()) {
            Block foundPath = getPath(path, pathId);
            if (foundPath != null) {
                return foundPath;
            }
        }
        return null;        
    }
    
    private Block getPath(Block path, int pathId) {
        if (pathId == path.getId()) {
            return path;
        }
        for (Block block : path.getPathBlocks()) {
            for (Block loopPath : block.getLoopPaths()) {
                block.incrementSeen();
                Block foundPath = getPath(loopPath, pathId);
                if (foundPath != null) {
                    return foundPath;
                }
                block.decrementSeen();
            }
        }
        return null;
    }
}
