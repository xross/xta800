package com.xmos.xta.core.engine.sourcemapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.xmos.libs.dwarfhandler.DebugEngine;
import com.xmos.libs.util.Pair;
import com.xmos.xta.core.engine.analysis.binary.InstructionDecoder;
import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;

public class SourceLine implements Line {
	private String             m_line;
	private long               m_lineNumber;
	private List<Long>         m_pcs;
	private SourceMapper       m_sourceMapper;
	private SourceFile         m_sourceFile;
	private DebugEngine        m_debugEngine;
	private InstructionDecoder m_instructionDecoder;
	
	public SourceLine(String line, long lineNumber, 
			SourceMapper sourceMapper, SourceFile sourceFile, 
			DebugEngine debugEngine, InstructionDecoder instructionDecoder) {
		
	    assert(line != null);
	    assert(lineNumber > 0);
		m_line = line;
		m_lineNumber = lineNumber;
		m_sourceMapper = sourceMapper;
		m_sourceFile = sourceFile;
		m_debugEngine = debugEngine;
		m_instructionDecoder = instructionDecoder;
	}

	public void addPc(long pc) {
		m_pcs.add(pc);
	}

	public Collection<Long> getPcs() {
		if (m_pcs == null) {
			initialisePcsMap();
		}		
		return m_pcs;
	}
	
	public long getLineNumber() {
		return m_lineNumber;
	}

    public String getText() {
        return m_line;
    }
    
    private void initialisePcsMap() {
    	m_pcs = new ArrayList<Long>();
    	
		Collection<Pair<Long,Long>> addressRanges = m_debugEngine.getAddressRangesMatchingPath(
				m_sourceFile.getFullFileName(), m_lineNumber);
       	Collection<Long> addresses = rangesToAddresses(addressRanges);

        for (long pc : addresses) {
            if (m_sourceMapper.getFunction(pc) != null) {
              	m_pcs.add(pc);
            }
        }
    }

    private Collection<Long> rangesToAddresses(Collection<Pair<Long, Long>> addressRanges) {
		Set<Long> addresses = new TreeSet<Long>();
		for (Pair<Long, Long> range : addressRanges) {
			long endPc = m_instructionDecoder.getEndPc();
			for (long pc = range.first; (pc < range.second) && (pc <= endPc); pc += 2) {
				InstructionNode node = m_instructionDecoder.getNode(pc);
				if (node != null) {
					addresses.add(pc);
				}
			}
		}
		return addresses;
	}
}


