package com.xmos.xta.core.engine.sections;

import com.xmos.xta.core.engine.utils.NodeCorePair;

public interface FileLineInfo extends ScriptableInfo  {
	public String getCompilationDir();
	public String getFileName();
    public long getLineNumber();
    public NodeCorePair getNodeCorePair();
}
