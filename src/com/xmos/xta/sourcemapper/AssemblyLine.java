package com.xmos.xta.sourcemapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.xmos.xta.engine.analysis.structural.nodes.InstructionNode;
import com.xmos.xta.engine.instructions.XtaInstruction;

public class AssemblyLine implements Line {	
	
	private Map<String, Long> m_labels;
	private static final int WIDTH = 20;
	
	public static String getLeadingWhiteSpace(){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i< WIDTH; i++){
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
	
	public AssemblyLine(InstructionNode instructionNode, Map<String, Long> labels) {
		m_instructionNode = instructionNode;
		m_labels = labels;
	}

	public static String [] getResourceNames(){
		return m_resourceName;
	}

	
	public List<AssemblyLine> getChildern() {
		return new LinkedList<AssemblyLine>();
	}

	public long getLineNumber() {
		return m_instructionNode.getXtaInstruction().getPc();
	}

	public String getPreText(){
		
		String preText = "";
		
		for(String key : m_labels.keySet()){
    		if(m_instructionNode.getXtaInstruction().getPc() == m_labels.get(key)){
    			preText = key+":";
    			break;
    		}
    	}
		
		if(preText.length() > WIDTH){
			preText = preText.substring(0, WIDTH);
		}
		
		for(int i=preText.length(); i< WIDTH; i++){
			preText += " ";
		}
		
		return preText;
	}
	public String getEmptyText(){
		String preText = "";
		
		for(int i=preText.length(); i< WIDTH; i++){
			preText += " ";
		}
		
		return preText;
	}
	
	public String getText() {

		XtaInstruction inst  = m_instructionNode.getXtaInstruction();
		String operandString = inst.getOperandString();
		if (inst.getNmenonic().toLowerCase().equals("getr (rus)")) {
			for (int i = 0; i < 12; i++) {
				String lookUp = "i 0x"+ Integer.toHexString(i);
				if (inst.getOperandString().contains(lookUp)) {
					operandString = operandString.substring(0, operandString.indexOf(lookUp)) + m_resourceName[i];
				}
			}
		} else if (inst.getNmenonic().toLowerCase().equals("ldapf (u10)")) {
			String offset = (operandString.substring(operandString.indexOf("0x")+2));
			long loadLocation = (Long.parseLong(offset, 16) << 1) + inst.getPc() + inst.getNumBytes();
			operandString = operandString.substring(0, operandString.indexOf(",")) + ", <0x" + Long.toHexString(loadLocation) + ">";
		} else if (inst.getNmenonic().toLowerCase().equals("ldapb (u10)")) {
			String offset = (operandString.substring(operandString.indexOf("0x")+2));
			long loadLocation = inst.getPc()-(Long.parseLong(offset, 16) << 1) + inst.getNumBytes();;
			operandString = operandString.substring(0, operandString.indexOf(",")) + ", <0x" + Long.toHexString(loadLocation) + ">";
		} else if (inst.getNmenonic().toLowerCase().equals("chkct (rus)")) {
			for (int i = 0; i < 12; i++){
				String lookUp = "i 0x"+Integer.toHexString(i);
				if (inst.getOperandString().contains(lookUp)){
					operandString = operandString.substring(0, operandString.indexOf(lookUp)) + m_controlTokenName[i] ;
				}
			}
		} else if (inst.getNmenonic().toLowerCase().equals("outct (rus)")) {
			for (int i = 0; i < 12; i++){
				String lookUp = "i 0x" + Integer.toHexString(i);
				if (inst.getOperandString().contains(lookUp)){
					operandString = operandString.substring(0, operandString.indexOf(lookUp)) + m_controlTokenName[i] ;
				}
			}
		}
	    if (inst.getBranchPcRelative()) {
	        String functionName = inst.getBranchPcRelativeTargetFunction();
	        
	        String target = functionName;
	        
	        if(functionName.startsWith("0x")){
	        	for(String key : m_labels.keySet()){
	        		if(inst.getBranchPcRelativeTargetPc() == m_labels.get(key)){
	        			target = key;
	        			break;
	        		}
	        	}
	        } 
	        if(target == null){
	        	target = functionName;
	        }
	        
	        if(operandString.contains("i ")){
	        	operandString = operandString.substring(0, operandString.indexOf("i "));
	        }
	        operandString = operandString.trim();
	        return "\t" + inst.getNmenonic() + " " + operandString +
	        		" <" + target + ">"; 
	    } else {
	        return "\t" + inst.getNmenonic() +" " + operandString;
	    }
	}

	public long getPc() {
		return m_instructionNode.getXtaInstruction().getPc();
	}
	
}