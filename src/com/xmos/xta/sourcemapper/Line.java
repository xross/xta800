package com.xmos.xta.sourcemapper;

import java.util.List;

public interface Line {
	public String getText();
	public List<AssemblyLine> getChildern();
	public int getLineNumber();
}
