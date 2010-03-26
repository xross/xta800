package com.xmos.xta.core.engine.sourcemapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xmos.libs.dwarfhandler.DebugEngine;
import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;

public class SourceFile implements Comparable<SourceFile> {
	private SourceMapper m_sourceMapper;	
	private DebugFile m_debugFile;
	private DebugEngine m_debugEngine;
	private InstructionDecoder m_instructionDecoder;
	private ArrayList<SourceLine> m_sourceLines;
	private Map<Long, SourceLine> m_pcToLine;

	public SourceFile(SourceMapper sourceMapper, DebugFile debugFile, 
			DebugEngine debugEngine, InstructionDecoder instructionDecoder) {
		
		m_sourceMapper = sourceMapper;
		m_debugFile = debugFile;
		m_debugEngine = debugEngine;
		m_instructionDecoder = instructionDecoder;
		m_sourceLines = new ArrayList<SourceLine>();
	}

	public String getFileName() {
		return m_debugFile.getFileName();
	}

	public String getFullFileName() {
		return m_debugFile.getFullFileName();
	}

	public void readFile() {
		try {
			readFile(m_debugFile.getFile());
		} catch (IOException e) {}
	}

	public List<SourceLine> getSourceLines() {
		return m_sourceLines;
	}

	public File getFile() {
		return m_debugFile.getFile();
	}


	//GUI only
	public SourceLine getLineForPc(long pc) {
		if (m_pcToLine == null) {
			initialisePcToLineMap();
		}		
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

	public boolean okToWrite(SourceMapper sourceMapper) {
		File file = m_debugFile.getFile();
		if ((file == null) || !file.canWrite() || sourceMapper.fileNewerThanBinary(file)) {
			return false;
		}
		return true;
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
			
			SourceLine newLine = new SourceLine(line, lineNo, m_sourceMapper, 
					this, m_debugEngine, m_instructionDecoder);
			
			m_sourceLines.add(newLine);
			lineNo++;
		}
		inputFp.close();
	}	

	//GUI only
	private void initialisePcToLineMap() {
		m_pcToLine = new NonIteratableMap<Long, SourceLine>();
		
    	for (SourceLine sourceLine : m_sourceLines) {
        	for (long statementPc : sourceLine.getStatementPcs()) {
        		m_pcToLine.put(statementPc, sourceLine);
        	}
        }
	}
}
