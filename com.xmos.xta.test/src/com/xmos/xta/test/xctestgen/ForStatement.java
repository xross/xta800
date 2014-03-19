package com.xmos.xta.test.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class ForStatement extends CodeElement {

    public ForStatement(int id) {
        super(1, "f", id);
    }

    public void emitSource(BufferedWriter file, int depth) throws IOException  {
        emitSourceIndent(file, depth);
        file.write("for (int i" + m_id + " = 0; i" + m_id + " < temp; i" + m_id + "++) {\n");
        for (CodeElement child : getChildren()) {
            child.emitSource(file, depth + 1);
        }
        emitSourceIndent(file, depth);
        file.write("}\n");
    }

    public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {

        emitExpectIndent(file, depth);
        file.write("loop\n");
        for (CodeElement child : m_children.get(node)) {
            child.emitExpect(node, file, depth+1);
        }
    }

    public int getNodeNumPaths() {
        return 1;
    }
}

