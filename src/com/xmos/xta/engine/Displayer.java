package com.xmos.xta.engine;

import com.xmos.xeloader.DebugInfo;
import com.xmos.xeloader.MemoryImage;
import com.xmos.xeloader.SymbolTable;

public interface Displayer {
	
	public static final String NOTIFY_ANALYSE_PATHS   = "AnalysePath";
	public static final String NOTIFY_DISPLAY_ROUTE   = "DisplayRoute";
	public static final String NOTIFY_ERROR           = "Error";
	public static final String NOTIFY_LOAD_EXECUTABLE = "LoadExecutable";
	public static final String NOTIFY_SELECT_FUNCTION = "SelectFunction";
	public static final String NOTIFY_SET_END_POINT   = "SetEndPoint";
	public static final String NOTIFY_SET_FOCUS       = "SetFocus";
	public static final String NOTIFY_SET_START_POINT = "SetStartPoint";
	public static final String NOTIFY_SHOW_CODE_POINT = "ShowCodePoint";
	public static final String NOTIFY_UPDATE_STATE    = "UpdateState";
	public static final String NOTIFY_UPDATE_EXCLUDES = "UpdateExcludes";
	
    void displayRoute(Route route, 
            DebugInfo debugInfo, MemoryImage memoryImage, SymbolTable m_symbolTable);
    
    void notify(String message, String argument);
}
