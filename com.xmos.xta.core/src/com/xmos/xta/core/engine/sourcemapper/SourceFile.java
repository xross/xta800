package com.xmos.xta.core.engine.sourcemapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xmos.libs.dwarfhandler.DebugFile;
import com.xmos.libs.instructiondecoder.InstructionDecoders;
import com.xmos.libs.util.CoreCollection;
import com.xmos.libs.util.CoreId;
import com.xmos.libs.util.NonIteratableMap;
import com.xmos.libs.xeloader.XeLoader;

public class SourceFile implements Comparable<SourceFile> {
	private DebugFile m_debugFile;
	private XeLoader m_xeLoader;
	private InstructionDecoders m_instructionDecoders;
	private ArrayList<SourceLine> m_sourceLines;
	private CoreCollection<Map<Long, SourceLine>> m_pcToLine;

	public SourceFile(DebugFile debugFile, XeLoader xeLoader, 
			InstructionDecoders instructionDecoders) {
		
		m_debugFile = debugFile;
		m_xeLoader = xeLoader;
		m_instructionDecoders = instructionDecoders;
		m_sourceLines = new ArrayList<SourceLine>();
		m_pcToLine = new CoreCollection<Map<Long, SourceLine>>();
	}

	public void readFile() {
		try {
			readFile(m_debugFile.getFile());
		} catch (IOException e) {}
	}
	
	public String getFileName() {
		return m_debugFile.getFileName();
	}

	public String getFullFileName() {
		return m_debugFile.getFullFileName();
	}

	public File getFile() {
		return m_debugFile.getFile();
	}

	public DebugFile getDebugFile() {
		return m_debugFile;
	}

	public List<SourceLine> getSourceLines() {
		return m_sourceLines;
	}
	
	public SourceLine getLineForPc(CoreId coreId, long pc) {
		if (m_pcToLine.get(coreId) == null) {
			initialisePcToLineMap(coreId);
		}
		return m_pcToLine.get(coreId).get(pc);
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
			// Replace tabs with soft tabs
			line = line.replaceAll("\t", "    ");
			
			SourceLine newLine = new SourceLine(line, lineNo,
					this, m_xeLoader, m_instructionDecoders);
			
			m_sourceLines.add(newLine);
			lineNo++;
		}
		inputFp.close();
	}	

	private void initialisePcToLineMap(CoreId coreId) {
		Map<Long, SourceLine> pcToLine = new NonIteratableMap<Long, SourceLine>();
		
    	for (SourceLine sourceLine : m_sourceLines) {
        	for (long statementPc : sourceLine.getStatementPcs(coreId)) {
        		pcToLine.put(statementPc, sourceLine);
        	}
        }
    	m_pcToLine.add(coreId, pcToLine);
	}
}
