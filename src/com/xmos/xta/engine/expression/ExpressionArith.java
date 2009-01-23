package com.xmos.xta.engine.expression;

public class ExpressionArith extends Expression {
	
	public enum OperatorType {
		ADD, SUB, AND, ANDNOT, ASHR, ASHL, BITREV, BYTEREV, CLZ, CRC32, CRC8_SCRDEST, CRC8_DEST, DIVS, DIVU, LTS, LTU, GT, NEG, NOT, OR, EQ, MACCU_LOW, MACCU_HIGH, MACCS_LOW, MACCS_HIGH, SHL, SHR, REMS, REMU, SEXT, ZEXT, XOR, MUL, LSUB_LOWER, LSUB_CARRY, LADD_LOWER, LADD_CARRY, LDIVU_LOW, LDIVU_HIGH,
	}
	
	
	
	public ExpressionArith(OperatorType operator){
		
		
		super();
	}
	
}
