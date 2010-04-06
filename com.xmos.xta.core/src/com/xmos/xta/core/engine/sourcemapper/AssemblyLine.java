package com.xmos.xta.core.engine.sourcemapper;

import java.util.Map;
import java.util.Set;

import com.xmos.xta.core.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.core.engine.instructions.XtaInstruction;

public class AssemblyLine implements Line {

	private Map<String, Set<Long>> m_labels;
	private static final int WIDTH = 20;

	//GUI only
	public static String getLeadingWhiteSpace() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < WIDTH; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	private static final String m_resourceName[] = {
		"PORT", "TIMER", "CHANEND",	"SYNC", "THREAD",
		"LOCK", "CLKBLK", "COPROC", "INVALID", "INVALID",
		"INVALID", "PS", "CONFIG", "INSTRUCTION",
	};

	private static final String m_controlTokenName[] = {
		"INVALID", "CT_END","CT_PAUSE",	"CT_ACK","CT_NACK",
		"INVALID", "INVALID", "INVALID", "INVALID", "INVALID",
		"INVALID", "INVALID", "INVALID", "INVALID", "INVALID",
		"INVALID", "CT_READN", "CT_READ1", "CT_READ2", "CT_READ4",
		"CT_READ8", "CT_WRITEN", "CT_WRITE1", "CT_WRITE2",
		"CT_WRITE4", "CT_WRITE8", "CT_CALL", "INVALID", "INVALID",
		"INVALID", "INVALID", "INVALID",
	};

	private InstructionNode m_instructionNode;

	public AssemblyLine(InstructionNode instructionNode, Map<String, Set<Long>> labels) {
		m_instructionNode = instructionNode;
		m_labels = labels;
	}
	
	//GUI only
	public static String [] getResourceNames(){
		return m_resourceName;
	}

	public long getLineNumber() {
		return m_instructionNode.getXtaInstruction().getPc();
	}

	//GUI only
	public String getPreText() {
		StringBuffer preText = new StringBuffer();
		for (String key : m_labels.keySet()) {
    		if (m_labels.get(key).contains(m_instructionNode.getXtaInstruction().getPc())) {
    			preText.append(key);
    			preText.append(":");
    			break;
    		}
    	}

		if (preText.length() > WIDTH) {
			preText.setLength(WIDTH);
			//preText = preText.substring(0, WIDTH);
		}

		for (int i = preText.length(); i < WIDTH; i++) {
			preText.append(" ");
		}

		return preText.toString();
	}

	//GUI only
	public String getEmptyText() {
		StringBuffer preText = new StringBuffer();
		for (int i = preText.length(); i < WIDTH; i++) {
			preText.append(" ");
		}
		return preText.toString();
	}

	//GUI only
	public String getText() {
		XtaInstruction inst  = m_instructionNode.getXtaInstruction();
		String operandString = inst.getOperandString();
		if (inst.getMnemonic().toLowerCase().equals("getr (rus)")) {
			String offset = (operandString.substring(operandString.indexOf("0x") + 2));
			int resNum = Integer.parseInt(offset, 16);
			operandString = operandString.substring(0, operandString.indexOf(", 0x")) + " " + m_resourceName[resNum];

		} else if (inst.getMnemonic().toLowerCase().equals("ldap (u10)")) {
			String offset = (operandString.substring(operandString.indexOf("0x") + 2));
			long loadLocation = inst.getPc() + inst.getNumBytes();
			if (operandString.charAt(operandString.indexOf("0x") - 1) == '-') {
				loadLocation = loadLocation - scale(Long.parseLong(offset, 16));
			} else {
				loadLocation = loadLocation + scale(Long.parseLong(offset, 16));
			}
			operandString = operandString.substring(0, operandString.indexOf(",")) + ", <0x" + Long.toHexString(loadLocation) + ">";

		} else if (inst.getMnemonic().toLowerCase().equals("chkct (rus)")) {
			String offset = (operandString.substring(operandString.indexOf("0x") + 2));
			int ctNum = Integer.parseInt(offset, 16);
			operandString = operandString.substring(0, operandString.indexOf(", 0x")) + ", " + m_controlTokenName[ctNum] ;

		} else if (inst.getMnemonic().toLowerCase().equals("outct (rus)")) {
			String offset = (operandString.substring(operandString.indexOf("0x") + 2));
			int ctNum = Integer.parseInt(offset, 16);
			operandString = operandString.substring(0, operandString.indexOf(", 0x")) + ", " + m_controlTokenName[ctNum] ;

		}

	    if (inst.getBranchPcRelative()) {
	        String functionName = inst.getBranchPcRelativeTargetFunction();
	        String target = functionName;

	        if (functionName.startsWith("0x")) {
	        	for (String key : m_labels.keySet()) {
	        		if (m_labels.get(key).contains(inst.getBranchPcRelativeTargetPc())) {
	        			target = key;
	        			break;
	        		}
	        	}
	        }
	        if (target == null) {
	        	target = functionName;
	        }

	        operandString = operandString.trim();
	        return "\t" + inst.getMnemonic() + " " + operandString + " <" + target + ">";

	    } else {
	        return "\t" + inst.getMnemonic() +" " + operandString;
	    }
	}

	//GUI only
	private long scale(long x) {
		return x << 1;
	}

	//GUI only
	public long getPc() {
		return m_instructionNode.getXtaInstruction().getPc();
	}

}