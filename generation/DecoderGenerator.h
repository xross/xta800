/*
 * Copyright XMOS Limited - 2005
 */

#ifndef _DECODER_GENERATOR_H_
#define _DECODER_GENERATOR_H_

#include <string>
#include "XmosUtils.h"

class DbInterface;
class DbMacro;
class DbInstruction;
class DbFormat;
class DecodeNode;

using namespace std;

class DecoderGenerator
{
public:
	DecoderGenerator(const char *fileName, DbInterface &dbInterface, 
                   XmosUtils *xmosUtils, const char *version);
	virtual ~DecoderGenerator();

private:
	/* The copy and assignment constructors are not supported and are not implemented */
	DecoderGenerator(const DecoderGenerator &x);
	DecoderGenerator& operator=(const DecoderGenerator &x);

private:
  void generateHeader(FILE *outFp);
  void generateTree(FILE *outFp, DecodeNode *node, bool isStart);
  void printIndent(FILE *outFp);

	/* Deal with outputting to a file */
	void printNode(FILE *outFp, DecodeNode *node);
	void printNodeDecision(FILE *outFp, DecodeNode *node, DecodeNode *subNode);
	void printField(FILE *outFp, DecodeNode *node);

	/* Deal with indentation */
	void printIndent();
	void setIndent(int indent);
	void incIndent();
	void decIndent();

	/* Deal with the calculation of the branch target pcs */
  void calculateBranchTargetInfo(FILE *outFp, DbFormat *fmt, DbInstruction *inst);
  string *preProcess(const string *txt);

private:
	DbInterface &m_dbInterface;
  XmosUtils *m_xmosUtils;
	int m_indent;
  const char *m_version;
};

#endif /* _DECODER_GENERATOR_H_ */
