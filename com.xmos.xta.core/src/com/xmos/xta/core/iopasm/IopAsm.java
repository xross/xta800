package com.xmos.xta.core.iopasm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.xmos.xta.core.iopasm.instructions.Instruction;
import com.xmos.xta.core.iopasm.instructions.InstructionFactory;

public class IopAsm {
	
	static public void main(String[] args) {
		for (String arg : args) {
			readFile(arg);
		}
		System.exit(0);
	}

	static void readFile(String fileName) {
		System.out.println("Parsing file " + fileName);
        try {
            BufferedReader fp = new BufferedReader(new FileReader(fileName));
            Program program = new Program(fileName);

            try {
            	int lineNumber = 1;
                String line;

                while ((line = fp.readLine()) != null) {
                    String comment = "";
                	
                	int commentIndex = line.indexOf("//");
                	if (commentIndex != -1) {
                		comment = line.substring(commentIndex + 2);
                		line = line.substring(0, commentIndex);
                	}

                	line = line.trim();
                    if (line.length() > 0) {
                    	Instruction instruction = InstructionFactory.createInstruction(line, lineNumber, comment);
                    	program.addInstruction(instruction);
                    }
                    ++lineNumber;
                }
                fp.close();
                
            } catch (IOException e) {
       			System.err.println("error: problem reading the file: " + fileName);
            }
    		program.printReport();
        } catch (IOException e) {
   			System.err.println("error: unable to open file for reading: " + fileName);
        }

	}
}
