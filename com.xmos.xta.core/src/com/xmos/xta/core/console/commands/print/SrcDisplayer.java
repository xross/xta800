package com.xmos.xta.core.console.commands.print;

import java.util.List;
import java.util.Set;

import com.xmos.libs.core.instructiondecoder.Instruction;
import com.xmos.libs.core.utils.CoreId;
import com.xmos.libs.core.utils.NonIteratableMap;
import com.xmos.libs.core.utils.NonIteratableSet;
import com.xmos.xta.core.console.Console;
import com.xmos.xta.core.engine.Engine;
import com.xmos.xta.core.engine.analysis.structural.nodes.Node;
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

    public void display(Node node, CoreId coreId) {
          List<Instruction> instructions = node.getInstructions();
        SourceMapper sourceMapper = m_engine.getSourceMapper();
        NonIteratableMap<String, Set<Long>> linesCovered =
            createSrcLinesCovered(instructions, sourceMapper, coreId);

        for (SourceFile sourceFile : sourceMapper.getSourceFiles()) {
            Set<Long> fileLines = linesCovered.get(sourceFile.getFullFileName());
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

    private NonIteratableMap<String, Set<Long>> createSrcLinesCovered(List<Instruction> instructions,
            SourceMapper sourceMapper, CoreId coreId) {

        NonIteratableMap<String, Set<Long>> linesCovered = new NonIteratableMap<String, Set<Long>>();
        for (Instruction inst : instructions) {
            for (SourceFile sourceFile : sourceMapper.getSourceFiles()) {

                String compilationName = sourceFile.getFullFileName();
                Set<Long> sourceLines;
                if (linesCovered.containsKey(compilationName)) {
                    sourceLines = linesCovered.get(compilationName);
                } else {
                    sourceLines = new NonIteratableSet<Long>();
                    linesCovered.put(compilationName, sourceLines);
                }

                for (SourceLine sourceLine : sourceFile.getSourceLines()) {
                    if (sourceLine.getPcs(coreId).contains(inst.getPc())) {
                        sourceLines.add(sourceLine.getLineNumber());
                    }
                }
            }
        }
        return linesCovered;
    }
}
