/*
 * Copyright XMOS Limited - 2005
 */

#include "DecoderGenerator.h"
#include "DbInterface.h"
#include "InstructionClassGenerator.h"
#include "XmosUtils.h"
#include "StringLibrary.h"

void PrintUsage()
{
    printf("Usage:\n");
    printf("  XtaGeneration <db> <version>\n");
}

//
// This is the tool that generates all the xta java source code from the database.
// It uses the database reader to get all the information from the actual database
// files
//
int main(int argc, char **argv)
{
  int returnValue = 0;

  XmosUtils *xmosUtils = SAFE_NEW(XmosUtils);
  memset(xmosUtils, 0, sizeof(XmosUtils));
  
  xmosUtils->errorHandler = SAFE_NEW(ErrorHandler());
  xmosUtils->stringLibrary = SAFE_NEW(StringLibrary(xmosUtils->errorHandler));
  
  if (argc == 3)
  {
    DbInterface dbInterface(argv[1], xmosUtils);

    printf("Parsed database with %d instructions, %d formats, %d fields and %d operands\n",
      dbInterface.numInstructions(), dbInterface.numFormats(),
      dbInterface.numFields(), dbInterface.numOperands());

    DecoderGenerator decoderGenerator("XtaInstructionDecoder.java", dbInterface, xmosUtils, argv[2]);
		InstructionClassGenerator instructionClassGen("XtaInstructions.java", dbInterface, xmosUtils, argv[2]);
  }
  else
  {
    PrintUsage();
    returnValue = 1;
  }
  
  SAFE_DELETE(xmosUtils);
  return returnValue;
}

