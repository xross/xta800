package com.xmos.xta.sourcemapper;

import java.util.List;

public interface Line {
    enum DisplayType {
        hex,
        decimal,
        none
    }
    
	public String getText();
	public List<Line> getChildern();
	public int getLineNumber();
	public DisplayType getLineNumberType();
	public boolean isExcluded();
}
