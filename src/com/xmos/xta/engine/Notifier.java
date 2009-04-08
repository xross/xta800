package com.xmos.xta.engine;

public interface Notifier {
	public static final String NOTIFY_ERROR              = "Error";
	public static final String NOTIFY_WARNING            = "Warning";
	public static final String NOTIFY_INFO               = "Info";	
	public static final String NOTIFY_RUN                = "Run";
	public static final String NOTIFY_SET_RUN_MODE       = "SetRunMode";
	public static final String NOTIFY_SET_GUI_COMMAND    = "SetGuiCommand";
	public static final String NOTIFY_ANALYSIS_BEGIN     = "AnalysisBegin";
	public static final String NOTIFY_ANALYSIS_COMPLETE  = "AnalysisComplete";	
	public static final String NOTIFY_LOAD_EXECUTABLE    = "LoadExecutable";
	public static final String NOTIFY_SET_NODE_CORE      = "SetNodeCore";
	public static final String NOTIFY_SELECT_FUNCTION    = "SelectFunction";
	public static final String NOTIFY_SELECT_ROUTE       = "SelectRoute";
	public static final String NOTIFY_REMOVE_ROUTE       = "RemoveRoute";
	public static final String NOTIFY_SET_START_POINT    = "SetStartPoint";
	public static final String NOTIFY_SET_END_POINT      = "SetEndPoint";
	public static final String NOTIFY_SET_CONSTRAINT     = "SetConstraint";
	public static final String NOTIFY_SET_FOCUS          = "SetFocus";
	public static final String NOTIFY_SHOW_CODE_POINT    = "ShowCodePoint";
	public static final String NOTIFY_SELECT_PATHS         = "ShowPaths";
	public static final String NOTIFY_UPDATE_STATE       = "UpdateState";
	public static final String NOTIFY_UPDATE_EXCLUDES    = "UpdateExcludes";
	public static final String NOTIFY_UPDATE_UNKNOWNS    = "UpdateUnknowns";
	public static final String NOTIFY_HIGHLIGHTS_UPDATED = "HighlightsUpdated";
	    
	public static final String STATE_NUM_THREADS = "NumThreads";
	public static final String STATE_FREQUENCY   = "Frequency";
	
	public static final String RUN_MODE_FUNCTION = "Analyse function";
	public static final String RUN_MODE_PATH     = "Analyse path";
	
    void notify(String message, String argument);
}
