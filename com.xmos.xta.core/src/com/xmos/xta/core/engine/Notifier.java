package com.xmos.xta.core.engine;

public interface Notifier {
	public static final String NOTIFY_ERROR   = "Error";
	public static final String NOTIFY_WARNING = "Warning";
	public static final String NOTIFY_INFO    = "Info";

	public static final String NOTIFY_LOAD_EXECUTABLE   = "LoadExecutable";
	public static final String NOTIFY_ANALYSIS_COMPLETE = "AnalysisComplete";

	public static final String NOTIFY_FROM_END_POINT_SET = "FromEndPointSet";
	public static final String NOTIFY_TO_END_POINT_SET   = "ToEndPointSet";

	public static final String NOTIFY_SELECT_ROUTE  = "SelectRoute";
	public static final String NOTIFY_REMOVE_ROUTE  = "RemoveRoute";
	public static final String NOTIFY_ROUTE_CHANGED = "RouteChanged";

	public static final String NOTIFY_UPDATE_STATE        = "UpdateState";
	public static final String NOTIFY_UPDATE_EXCLUSIONS   = "UpdateExclusions";
	public static final String NOTIFY_UPDATE_UNKNOWNS     = "UpdateUnknowns";
	public static final String NOTIFY_UPDATE_ACTIVE_CORES = "UpdateActiveCores";	

	public static final String CLEAR_LOG         = "ClearLog";
	public static final String HOLD_MESSAGES     = "HoldMessages";
	public static final String RELEASE_MESSAGES  = "ReleaseMessages";

	void notify(CommandSource src, String message, String argument);
}
