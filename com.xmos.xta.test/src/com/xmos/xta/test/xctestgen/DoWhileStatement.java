package com.xmos.xta.test.xctestgen;

import java.io.BufferedWriter;
import java.io.IOException;

public class DoWhileStatement extends CodeElement {

    public DoWhileStatement(int id) {
        super(1, "d", id);
    }

    public void emitSource(BufferedWriter file, int depth) throws IOException  {
        emitSourceIndent(file, depth);
        file.write("do {\n");
        for (CodeElement child : getChildren()) {
            child.emitSource(file, depth + 1);
        }
        emitSourceIndent(file, depth + 1);
        file.write("x--;\n");
        emitSourceIndent(file, depth);
        file.write("} while (x < " + m_id + ");\n");
    }

    public void emitExpect(int node, BufferedWriter file, int depth) throws IOException  {
        emitExpectIndent(file, depth);
        file.write("selfloop " + getName() + "\n" );
        int indent = 1;
        if(getChildCount() > 1){
            indent++;
            emitExpectIndent(file, depth+1);
            file.write("seq\n");
        }
        for (CodeElement child : m_children.get(node)) {
            child.emitExpect(node, file, depth+indent);
        }
    }

    public int getNodeNumPaths() {
        return 1;
    }
}

