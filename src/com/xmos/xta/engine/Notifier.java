package com.xmos.xta.engine;

public interface Notifier {
	public static final String NOTIFY_ERROR = "Error";
	public static final String NOTIFY_WARNING = "Warning";
	public static final String NOTIFY_INFO = "Info";

	public static final String NOTIFY_LOAD_EXECUTABLE = "LoadExecutable";

	public static final String NOTIFY_ANALYSIS_BEGIN    = "AnalysisBegin";
	public static final String NOTIFY_ANALYSIS_COMPLETE = "AnalysisComplete";
	public static final String NOTIFY_ANALYSIS_ABORTED  = "AnalysisAborted";

	public static final String NOTIFY_SET_FUNCTION_NAME = "SetFunctionName";
	public static final String NOTIFY_SET_START_POINT   = "SetStartPoint";
	public static final String NOTIFY_SET_END_POINT     = "SetEndPoint";

	public static final String NOTIFY_SET_NODE_CORE = "SetNodeCore";
	public static final String NOTIFY_SELECT_ROUTE  = "SelectRoute";
	public static final String NOTIFY_REMOVE_ROUTE  = "RemoveRoute";

	public static final String NOTIFY_UPDATE_STATE      = "UpdateState";
	public static final String NOTIFY_UPDATE_EXCLUSIONS = "UpdateExclusions";
	public static final String NOTIFY_UPDATE_UNKNOWNS   = "UpdateUnknowns";

	public static final String STATE_NUM_THREADS = "NumThreads";
	public static final String STATE_FREQUENCY   = "Frequency";
	public static final String STATE_CASE        = "Case";

	public static final String RUN_MODE_FUNCTION = "Analyse function";
	public static final String RUN_MODE_PATH     = "Analyse path";
	
	public static final String HOLD_MESSAGES     = "Hold messages";
	public static final String RELEASE_MESSAGES  = "Release messages";
	
	void notify(CommandSource src, String message, String argument);
}
