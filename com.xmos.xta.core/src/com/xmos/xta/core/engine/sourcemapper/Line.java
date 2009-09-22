package com.xmos.xta.core.engine.sourcemapper;

import java.util.List;

public interface Line {
	public String getText();
	public List<AssemblyLine> getChildern();
	public long getLineNumber();
}
