package com.xmos.xta.core.console.commands.print;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
import com.xmos.xta.core.engine.instructions.XtaInstruction;
import com.xmos.xta.core.engine.sourcemapper.SourceFile;
import com.xmos.xta.core.engine.sourcemapper.SourceLine;
import com.xmos.xta.core.engine.sourcemapper.SourceMapper;

public class SrcDisplayer {
	private Engine m_engine;
	private Console m_console;

	public SrcDisplayer(Engine engine, Console console) {
		m_engine = engine;
		m_console = console;
	}

	public void display(Node node, int nodeNumber, int coreNumber) {
  	    List<XtaInstruction> instructions = node.getInstructions();
		SourceMapper sourceMapper = m_engine.getSourceMapper(nodeNumber, coreNumber);
        HashMap<String, TreeSet<Long>> linesCovered =
        	createSrcLinesCovered(instructions, sourceMapper);

		for (SourceFile sourceFile : sourceMapper.getSourceFiles()) {
	        TreeSet<Long> fileLines = linesCovered.get(sourceFile.getFullFileName());
	        if (fileLines.size() > 0) {
				m_console.display("----------------------------------\n");
				m_console.display(sourceFile.getFullFileName() + "\n");
				m_console.display("----------------------------------\n");

				for (SourceLine sourceLine : sourceFile.getSourceLines()) {
					if (fileLines.contains(sourceLine.getLineNumber())) {
						m_console.display(String.format("+"));
					}
					else {
	                   	m_console.display(String.format(" "));
					}
					m_console.display(String.format(" %d: %s\n",
							sourceLine.getLineNumber(),
							sourceLine.getText()));
				}
	        }
		}
    }

	private HashMap<String, TreeSet<Long>> createSrcLinesCovered(List<XtaInstruction> instructions,
			SourceMapper sourceMapper) {

		HashMap<String, TreeSet<Long>> linesCovered = new HashMap<String, TreeSet<Long>>();
		for (XtaInstruction inst : instructions) {
			for (SourceFile sourceFile : sourceMapper.getSourceFiles()) {

				String compilationName = sourceFile.getFullFileName();
				TreeSet<Long> sourceLines;
				if (linesCovered.containsKey(compilationName)) {
					sourceLines = linesCovered.get(compilationName);
				} else {
					sourceLines = new TreeSet<Long>();
					linesCovered.put(compilationName, sourceLines);
				}

				for (SourceLine sourceLine : sourceFile.getSourceLines()) {
					if (sourceLine.getPcs().contains(inst.getPc())) {					
						sourceLines.add(sourceLine.getLineNumber());
					}
				}
			}
		}
		return linesCovered;
	}
}
