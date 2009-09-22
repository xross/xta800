/*
 * Copyright XMOS Limited - 2005
 */

#ifndef _INSTRUCTION_CLASS_GENERATOR_H_
#define _INSTRUCTION_CLASS_GENERATOR_H_

#include <fstream>
#include "XmosUtils.h"
#include "XmosTypes.h"

class DbInterface;
class DbInstruction;
class DbFormat;

class InstructionClassGenerator
{
public:
  InstructionClassGenerator(const char *fileName, DbInterface &dbInterface, 
                            XmosUtils *xmosUtils, const char *version);
  virtual ~InstructionClassGenerator();

private:
  void generateHeader();
  void generateDefinitions();

  void generateClassDefStart(const DbInstruction *inst, const DbFormat *fmt);
  void generateGetOperandFieldMethod(const DbInstruction *inst, const DbFormat *fmt);
  void generateGetOperandStringMethod(const DbInstruction *inst, const DbFormat *fmt);
  void generateClassDefEnd(const DbInstruction *inst, const DbFormat *fmt);
  
  std::string *preProcess(const std::string *txt);

private:
  /* The copy and assignment constructors are not supported and are not implemented */
  InstructionClassGenerator(const InstructionClassGenerator &x);
  InstructionClassGenerator& operator=(const InstructionClassGenerator &x);

private:
  XmosUtils *m_xmosUtils;
  std::ofstream m_outFile;
  DbInterface &m_dbInterface;
  const char *m_version;
};

#endif /* _INSTRUCTION_CLASS_GENERATOR_H_ */
