package com.xmos.xta.core.engine.sourcemapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xmos.libs.dwarfhandler.DebugFile;

public class SourceFile implements Comparable<SourceFile> {
	private DebugFile m_debugFile;
	private ArrayList<SourceLine> m_sourceLines;
	private Map<Long, SourceLine> m_pcToLine;

	public SourceFile(DebugFile debugFile) {
		m_debugFile = debugFile;
		m_sourceLines = new ArrayList<SourceLine>();
		m_pcToLine = new HashMap<Long, SourceLine>();
	}

	public String getBaseDir() {
		return m_debugFile.getBaseDir();
	}

	public String getFileName() {
		return m_debugFile.getFileName();
	}

	public String getShortFileName() {
		return m_debugFile.getShortFileName();
	}
	
	public String getFullFileName() {
		return m_debugFile.getFullFileName();
	}
	
	public void readFile() {
		try {
			readFile(m_debugFile.getFile());
		} catch (IOException e) {
			// Do nothing
		}
	}
	
	private void readFile(File file) throws IOException {
		if (file == null)
			return;
		
		int lineNo = 1;
		BufferedReader inputFp = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = inputFp.readLine()) != null) {
			// replace tabs with soft tabs
			line = line.replaceAll("\t", "    ");
			SourceLine newLine = new SourceLine(line, lineNo);
			m_sourceLines.add(newLine);
			lineNo++;
		}
		inputFp.close();
	}

	public List<SourceLine> getSourceLines() {
		return m_sourceLines;
	}

	public File getFile() {
		return m_debugFile.getFile();
	}
	
	public void addPcToLine(long pc, SourceLine sourceLine) {
		m_pcToLine.put(pc, sourceLine);
	}

	public SourceLine getLineForPc(long pc) {
		return m_pcToLine.get(pc);
	}

	public DebugFile getDebugFile() {
		return m_debugFile;
	}

	public boolean fileNameMatches(String fileName) {
		return m_debugFile.fileNameMatches(fileName);
	}

	public int compareTo(SourceFile o) {
		return m_debugFile.compareTo(o.getDebugFile());
	}
}
