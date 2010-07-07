package com.xmos.xta.core.engine.sections;

import com.xmos.libs.util.CoreId;

public interface FileLineInfo extends ScriptableInfo  {
	public String getCompilationDir();
	public String getFileName();
    public long getLineNumber();
    public CoreId getCoreId();
}
