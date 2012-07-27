package com.xmos.xta.core.engine.utils;

import java.io.File;

import com.xmos.libs.core.utils.FileNameCompare;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;

public class FileNameMatcher {
	public static boolean isMatch(String dir, String name, String fileName, 
			String workspacePath, SourceMapper sourceMapper) {
		
		if (FileNameCompare.fileNamesMatch(dir, name, fileName))
			return true;

		if (workspacePath != null) {
			if (FileNameCompare.fileNamesMatch(dir, name, workspacePath + File.separator + fileName))
				return true;			
		}
		
		SourceFile sourceFile = sourceMapper.getSourceFile(fileName);
		if (sourceFile != null) {
		    if (sourceFile.getDebugFile().fileNameMatches(dir, name)) {
		        return true;
			}
		}
		return false;
	}

	public static boolean isMatch(String fileName1, String fileName2, 
			String workspacePath, SourceMapper sourceMapper) {
		
		if (FileNameCompare.fileNamesMatch(fileName1, fileName2)) {
			return true;
		}

		if (workspacePath != null) {
			if (FileNameCompare.fileNamesMatch(fileName1, workspacePath + File.separator + fileName2))
				return true;			
		}

		SourceFile sourceFile1 = sourceMapper.getSourceFile(fileName1);
		if (sourceFile1 != null) {
			SourceFile sourceFile2 = sourceMapper.getSourceFile(fileName2);
			if (sourceFile1 == sourceFile2)
				return true;
		}

		return false;
	}
}
