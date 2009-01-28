package com.xmos.xta.engine;

public interface Displayer {
	public static final String NOTIFY_ERROR             = "Error";
	public static final String NOTIFY_WARNING           = "Warning";
	public static final String NOTIFY_INFO              = "Info";	
	public static final String NOTIFY_ANALYSIS_BEGIN    = "AnalysisBegin";
	public static final String NOTIFY_ANALYSIS_COMPLETE = "AnalysisComplete";	
	public static final String NOTIFY_LOAD_EXECUTABLE   = "LoadExecutable";
	public static final String NOTIFY_SELECT_FUNCTION   = "SelectFunction";
	public static final String NOTIFY_SET_START_POINT   = "SetStartPoint";
	public static final String NOTIFY_SET_END_POINT     = "SetEndPoint";
	public static final String NOTIFY_SET_FOCUS         = "SetFocus";
	public static final String NOTIFY_SHOW_CODE_POINT   = "ShowCodePoint";
	public static final String NOTIFY_UPDATE_STATE      = "UpdateState";
	public static final String NOTIFY_UPDATE_EXCLUDES   = "UpdateExcludes";
	    
    void notify(String message, String argument);
}
