package com.xmos.xta.core.engine.instructions;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;


public class DataCollection {

	private long m_bytesSeen;
	/*
	LDAPB, LDAPF, LDAWCP, LDAWDP, LDAWSP, LDC, LDWCP, LDWCPL, 
	LDWDP, LDWSP, RETSP, STWDP,STWSP, BLACP, BLAT, BLRB,
	BLRF, BRBF, BRBT, BRBU, BRFF, BRFT, BRFU, ENTSP,
	EXTDP, EXTSP, KENTSP, KRESTSP,
	*/

	private List<InstructionData> m_instructionData;
	
	public DataCollection () {
		m_bytesSeen = 0;
		m_instructionData = new LinkedList<InstructionData>();
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldapb_u10.class, xs1bXtaInstruction_ldapb_lu10.class,"u10"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldapf_u10.class, xs1bXtaInstruction_ldapf_lu10.class,"u10"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldwcpl_u10.class, xs1bXtaInstruction_ldwcpl_lu10.class, "u10"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_blacp_u10.class, xs1bXtaInstruction_blacp_lu10.class, "u10"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_blrb_u10.class, xs1bXtaInstruction_blrb_lu10.class, "u10"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_blrf_u10.class, xs1bXtaInstruction_blrf_lu10.class, "u10"));
		
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldawcp_u6.class, xs1bXtaInstruction_ldawcp_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_retsp_u6.class, xs1bXtaInstruction_retsp_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_blat_u6.class, xs1bXtaInstruction_blat_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_brbu_u6.class, xs1bXtaInstruction_brbu_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_brfu_u6.class, xs1bXtaInstruction_brfu_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_entsp_u6.class, xs1bXtaInstruction_entsp_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_extdp_u6.class, xs1bXtaInstruction_extdp_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_extsp_u6.class, xs1bXtaInstruction_extsp_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_kentsp_u6.class, xs1bXtaInstruction_kentsp_lu6.class, "u6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_krestsp_u6.class, xs1bXtaInstruction_krestsp_u6.class, "u6"));
		
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldawdp_ru6.class, xs1bXtaInstruction_ldawdp_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldawsp_ru6.class, xs1bXtaInstruction_ldawsp_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldc_ru6.class, xs1bXtaInstruction_ldc_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldwcp_ru6.class, xs1bXtaInstruction_ldwcp_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldwdp_ru6.class, xs1bXtaInstruction_ldwdp_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_ldwsp_ru6.class, xs1bXtaInstruction_ldwsp_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_stwdp_ru6.class, xs1bXtaInstruction_stwdp_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_stwsp_ru6.class, xs1bXtaInstruction_stwsp_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_brbf_ru6.class, xs1bXtaInstruction_brbf_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_brbt_ru6.class, xs1bXtaInstruction_brbt_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_brff_ru6.class, xs1bXtaInstruction_brff_lru6.class, "ru6"));
		m_instructionData.add(new InstructionData(xs1bXtaInstruction_brft_ru6.class, xs1bXtaInstruction_brft_lru6.class, "ru6"));
	}
	
	public void checkInstruction(XtaInstruction inst){
		m_bytesSeen += inst.getNumBytes();
		for(InstructionData instructionData : m_instructionData){
			if(instructionData.isInstruction(inst)){
				break;
			}
		}
		
	}
	
	public void report(){
		int wrongCount = 0;
		int longCount = 0;
		int shortCount = 0;
		for(InstructionData id : m_instructionData){
			//if(id.getLongCount() != 0 || id.getShortCount() != 0){
			//if(id.getWrongCount()!= 0){
				id.printResults();
			//}
			
			wrongCount += id.getWrongCount();
			longCount += id.getLongCount();
			shortCount += id.getShortCount();
		}
		
		double total = round(((double)(wrongCount*200)/(double)m_bytesSeen ),1);
		double saving = round(((double)(longCount*200)/(double)m_bytesSeen ),1);
		System.out.println("Total Wasted Space (Bytes): " + (wrongCount*2) + " (" +((wrongCount*2)/1024) +"kb) of " 
				+ m_bytesSeen + " bytes (" + (m_bytesSeen/1024) + "kb) " + total + "%");
		System.out.println("Total longs: " + longCount + " total shorts: " + shortCount + " saving if all short " + (longCount*2)/1024 + "kb(" + saving +"%)");
	}
	
	double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	class InstructionData {
		private Class<?> m_longInstruction;
		private Class<?> m_shortInstruction;
		private int m_longCount;
		private int m_shortCount;
		private int m_couldHaveBeenAShort;
		private String m_encoding; 
		
		public InstructionData (Class<?> shortInstruction, Class<?> longInstruction, String encoding){
			m_longInstruction = longInstruction;
			m_shortInstruction = shortInstruction;
			m_longCount = 0;
			m_shortCount = 0;
			m_couldHaveBeenAShort = 0;	
			m_encoding = encoding;
		}
		
		public boolean isInstruction(XtaInstruction inst){
			if(m_longInstruction == inst.getClass() ){
				m_longCount++;
				couldHaveBeenShort(inst);
				return true;
			} else if(m_shortInstruction == inst.getClass() ){
				m_shortCount++;
				return true;
			} else {
				return false;
			}
		}

		private void couldHaveBeenShort(XtaInstruction inst) {
			
			if(m_encoding.equals("u10")){
				long operand1 = inst.getOperandField("op1");
				if(operand1 <= 1023){
					m_couldHaveBeenAShort++;
				}
				
			} else if(m_encoding.equals("u6")){
				long operand1 = inst.getOperandField("op1");
				if(operand1 <= 63){
					m_couldHaveBeenAShort++;
				}
				
			} else if(m_encoding.equals("ru6")){
				long operand1 = inst.getOperandField("op2");
				if(operand1 <= 63){
					m_couldHaveBeenAShort++;
				}
			} else {
				assert(false);
			}
		}
		
		public int getWrongCount(){
			return m_couldHaveBeenAShort;
		}
		
		public int getLongCount(){
			return m_longCount;
		}
		public int getShortCount(){
			return m_shortCount;
		}
		
		public void printResults(){
			
			double percentOfLongs = (((double)m_couldHaveBeenAShort*100) / ((double)m_longCount) );
			
			String longs;
			if(m_longCount == 0){
				longs = "--";
			} else {
				longs = round(percentOfLongs, 1) + "%";
			}
			
			int totalSpaceWasted = m_couldHaveBeenAShort * 2;
			
			System.out.printf("%30s   Long: %4d   Short: %4d   Wrong: %4d(%6s)   Total: %4d,   Wasted Space (Bytes): %d\n", 
					m_longInstruction.getSimpleName(), m_longCount, m_shortCount, 
					m_couldHaveBeenAShort, longs, (m_longCount + m_shortCount), totalSpaceWasted);
		}
		
		public double round(double d, int decimalPlace){
		    BigDecimal bd = new BigDecimal(Double.toString(d));
		    bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
		    return bd.doubleValue();
		  }

	}
	
}
