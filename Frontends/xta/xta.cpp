/*
 * Copyright XMOS Limited - 2008
 */

#include <stdlib.h>
#include <stdio.h>
#include <sstream>
#include "XBHandler.h"
#include "ElfHandler.h"
#include "XmosUtils.h"

#include "frontend.h"

using namespace std;

bool needToRun(int argc, char **argv);
bool xeContainsXtaCommands(char *xeName);

int main(int argc, char **argv)
{
  string jar;

  const char *jarName = "xta.jar";
  
  if (findJarInPath(jar, jarName))
  {
    string jvmArgString = "";
    const char *jvmArgs = getenv("JVM_ARGS");
    if (jvmArgs != NULL) 
        jvmArgString = jvmArgs;
    
    string args;
    getArgString(args, argc, argv);

    if (needToRun(argc, argv)) {
      string command = "java " + jvmArgString + " -jar \"" + jar + "\"" + args;
      int returnCode = run(command.c_str());
      return returnCode;
    } else {
      return 0;
    }
  }
  else
  {
    fprintf(stderr, "Cannot find tool '%s'\n", jarName);
    return 1;
  }
}

bool needToRun(int argc, char **argv)
{
  // In the case when being simply called with "load XE exit" from xcc check
  // whether there are xta commands embedded that need to be run
  if (argc == 4) {
    if ((strcmp(argv[1], "load") == 0) && 
        ((strcmp(argv[3], "-exit") == 0) ||
         (strcmp(argv[3], "exit") == 0))) {
      
      if (!xeContainsXtaCommands(argv[2])) {
        return false;
      }
    }
  }
  return true;
}

bool xeContainsXtaCommands(char *xeName)
{
  bool containsXtaCommands = false;
  
  XmosUtils *xmosUtils = SAFE_NEW(XmosUtils);
  memset(xmosUtils, 0, sizeof(XmosUtils));
  xmosUtils->errorHandler = SAFE_NEW(ErrorHandler());
  
  XBHandler *xbHandler = loadXBFromFile(xeName);
  if (xbHandler == NULL) {
    xmosUtils->errorHandler->XMOS_ERROR(true, "xta: error loading file: %s\n", xeName);
  }
  if (xbHandler->error_number != XB_NOERROR) {
    xmosUtils->errorHandler->XMOS_ERROR(true, "xta: error loading file %s: %s\n", 
      xeName, xbHandler->errorString);
  }

  XBSectorHolder *thisSec = getFirstSector(xbHandler);
  while ((thisSec != NULL) && !containsXtaCommands)
  {
    if ((thisSec->secType == XBSEC_ELF)) {
      unsigned char *imageData = getSectorImageData(thisSec);
      unsigned int imageNumBytes = (unsigned int)getSectorImageNumBytes(thisSec);

      std::string stringData((char*)imageData, (size_t)imageNumBytes);
      std::istringstream entryStream(stringData, std::istringstream::binary);
      ElfHandler elfHandler(xmosUtils->errorHandler, entryStream);

      ElfStrings *elfStrings = elfHandler.getStrings();
      
      ElfShdrVector *sectionHeaders = elfHandler.getSectionHeaders();
      for (uint sectionIndex = 0; sectionIndex < sectionHeaders->size() && !containsXtaCommands; sectionIndex++) {
        ElfSectionHeader *sectionHeader = (*sectionHeaders)[sectionIndex];
        string sectionName = sectionHeader->getName(elfStrings);

        if (sectionName == ".xtacommand") {
          containsXtaCommands = true;
        }
      }
    }
    thisSec = getNextSector(xbHandler, thisSec);
  }

  SAFE_DELETE(xmosUtils->errorHandler);
  SAFE_DELETE(xmosUtils);
  return containsXtaCommands;
}
