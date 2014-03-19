package com.xmos.xta.core.engine;

public abstract class Notifier {
    public static final String NOTIFY_ERROR   = "Error";
    public static final String NOTIFY_WARNING = "Warning";
    public static final String NOTIFY_INFO    = "Info";

    public static final String NOTIFY_LOAD_EXECUTABLE   = "LoadExecutable";
    public static final String NOTIFY_ANALYSIS_COMPLETE = "AnalysisComplete";

    public static final String NOTIFY_FROM_END_POINT_SET = "FromEndPointSet";
    public static final String NOTIFY_TO_END_POINT_SET   = "ToEndPointSet";
    public static final String NOTIFY_LOOP_POINT_SET     = "LoopPointSet";

    public static final String NOTIFY_SELECT_ROUTE  = "SelectRoute";
    public static final String NOTIFY_REMOVE_ROUTE  = "RemoveRoute";
    public static final String NOTIFY_ROUTE_CHANGED = "RouteChanged";

    public static final String NOTIFY_UPDATE_STATE        = "UpdateState";
    public static final String NOTIFY_UPDATE_UNKNOWNS     = "UpdateUnknowns";
    public static final String NOTIFY_UPDATE_ACTIVE_CORES = "UpdateActiveCores";

    public static final String CLEAR_LOG         = "ClearLog";
    public static final String HOLD_MESSAGES     = "HoldMessages";
    public static final String RELEASE_MESSAGES  = "ReleaseMessages";

    private Config m_config;

    abstract public void notify(CommandSource src, String message, String argument);

    public Notifier(Config config) {
        m_config = config;
    }

    protected String getLevel(String message) {
        if (m_config.getWarningsAsErrors()) {
            if (message.equals(NOTIFY_ERROR) ||
                message.equals(NOTIFY_WARNING)) {
                return NOTIFY_ERROR;
            } else {
                return message;
            }

        } else if (m_config.getErrorsAsWarnings()) {
            if (message.equals(NOTIFY_ERROR) ||
                message.equals(NOTIFY_WARNING)) {
                return NOTIFY_WARNING;
            } else {
                return message;
            }

        } else {
            return message;
        }
    }
}
