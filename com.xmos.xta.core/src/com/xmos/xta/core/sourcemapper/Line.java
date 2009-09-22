package com.xmos.xta.core.sourcemapper;

import java.util.List;

public interface Line {
	public String getText();
	public List<AssemblyLine> getChildern();
	public long getLineNumber();
}
