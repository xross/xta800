package com.xmos.xta.engine;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;

public interface Displayer {
    void displayRoute(Route route, 
            DebugInfo debugInfo, MemoryImage memoryImage, SymbolTable m_symbolTable);
    
    void notifyLoadExecutable();
    void notifyAnalysePaths();
    void notifyUpdateState();
}
