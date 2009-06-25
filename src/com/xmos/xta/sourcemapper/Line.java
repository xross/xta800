package com.xmos.xta.sourcemapper;

import java.util.List;

public interface Line {
	public String getText();
	public List<Line> getChildern();
	public int getLineNumber();
	public boolean isGreyedOut();
}
