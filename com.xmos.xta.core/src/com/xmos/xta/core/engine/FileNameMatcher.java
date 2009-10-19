package com.xmos.xta.core.engine;

import com.xmos.libs.util.FileNameCompare;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;

public class FileNameMatcher {
	public static boolean isMatch(String dir, String name, String fileName, SourceMapper sourceMapper) {
		if (FileNameCompare.fileNamesMatch(dir, name, fileName))
			return true;
		
		SourceFile sourceFile = sourceMapper.getSourceFile(fileName);
		if (sourceFile != null) {
		    if (sourceFile.getDebugFile().fileNameMatches(dir, name)) {
		        return true;							 
			}
		}
		return false;
	}

	public static boolean isMatch(String fileName1, String fileName2, SourceMapper sourceMapper) {
		if (FileNameCompare.fileNamesMatch(fileName1, fileName2)) {
			return true;
		}
		return false;
	}
}
