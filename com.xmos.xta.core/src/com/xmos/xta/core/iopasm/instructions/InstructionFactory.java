package com.xmos.xta.core.iopasm.instructions;

import java.util.ArrayList;
import java.util.List;

public class InstructionFactory {
	
	public enum ControlOp {
		WAIT,
		BRANCH_ONE,
		BRANCH_MANY
	}
	public static Instruction createInstruction(String line, int lineNumber, String comment) {
		String label = "";
		String labelOffset = "";

		int commaIndex = line.indexOf(',');
		int colonIndex = line.indexOf(':');
		if ((colonIndex != -1) && (colonIndex < commaIndex)) {
			int barIndex = line.indexOf('|');
			if (barIndex == -1 || barIndex > colonIndex) {
				label = line.substring(0, colonIndex);
			} else {
				label = line.substring(0, barIndex);
				labelOffset = line.substring(barIndex + 1, colonIndex);
			}
			label = label.trim();
			line = line.substring(colonIndex + 1);
		}
		
		List<String> unitStrings = splitOnCommas(line);
		if (unitStrings.size() > 6) {
			System.err.println(lineNumber + ":error: can't pase line due to number of commas (" + unitStrings.size() + ")");
			System.exit(1);
		}
		
		ControlOp controlOp = getControlOp(unitStrings);
		
		switch (controlOp) {
		case WAIT:
			return new Wait(label, labelOffset, unitStrings, lineNumber, comment);
		case BRANCH_ONE:
			return new BranchOne(label, labelOffset, unitStrings, lineNumber, comment);
		case BRANCH_MANY:
			return new BranchMany(label, labelOffset, unitStrings, lineNumber, comment);
		}
		assert(false);
		return null;
	}

	// Split on commas, but ignore those within brackets
	private static List<String> splitOnCommas(String line) {
		List<String> strings = new ArrayList<String>();
		
		while (line.length() > 0) {
			int commaIndex = findCommaIndex(line);
		
			if (commaIndex == -1) {
				String token = new String(line);
				token = token.trim();
				strings.add(token);
				line = "";
			} else {
				String token = line.substring(0, commaIndex);
				token = token.trim();
				strings.add(token);
				line = line.substring(commaIndex + 1);
			}
		}
		
		return strings;
	}

	private static int findCommaIndex(String line) {
		int offset = 0;
		int brackets = 0;
		
		while (true) {
			int commaIndex = line.indexOf(',', offset);
			int openIndex = line.indexOf('(', offset);
			int closeIndex = line.indexOf(')', offset);

			if (brackets == 0) {
				if (commaIndex == -1) {
					return -1;
				} else if (openIndex == -1 || commaIndex < openIndex) {
					return commaIndex;
				} else {
					offset = openIndex + 1;
					brackets += 1;
				}
				
			} else {
				if (closeIndex == -1) {
					return -1;
				} else if (openIndex == -1 || closeIndex < openIndex) {
					offset = closeIndex + 1;
					brackets -= 1;
				} else {
					offset = openIndex + 1;
					brackets += 1;
				}
			}
		}
	}

	private static ControlOp getControlOp(List<String> strings) {
		String controlOp = "";
		if (strings.size() > 4) {
			controlOp = strings.get(4);
		}

		if (controlOp.startsWith("wait")) {
			return ControlOp.WAIT;
			
		} else if (controlOp.startsWith("bra")) {
			int barIndex = controlOp.indexOf('|');
			int andIndex = controlOp.indexOf('&');
			if (barIndex != -1 && andIndex != -1) {
				return ControlOp.BRANCH_MANY;
			} else {
				return ControlOp.BRANCH_ONE;
			}
			
		} else {
			return ControlOp.BRANCH_ONE;
		}
	}
}
