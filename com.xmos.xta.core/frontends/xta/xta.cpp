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
void checkJavaVersion(int major, int minor);
int getInt(const char *str, int offset, int &number);

int main(int argc, char **argv)
{
  string jar;

  checkJavaVersion(1, 5);

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
  char *xe = 0;
  std::string arg;
  for (int i = 1; i < argc; i++) {
    arg = argv[i];
    if (arg == "load" || arg == "-load") {
      if (i+1 >= argc || xe) {
        return true;
      }
      i++;
      xe = argv[i];
    } else if (arg == "exit" || arg == "-exit") {
      return xe ? xeContainsXtaCommands(xe) : false;
    } else if (arg == "print" || arg == "-print") {
      if (i+1 >= argc) {
        return true;
      }
      i++;
      arg = argv[i];
      if (arg != "warnings") {
        return true;
      }
    } else if (arg == "config" || arg == "-config") {
      if (i+1 >= argc) {
        return true;
      }
      i++;
      arg = argv[i];
      if (arg != "Terror" && arg != "Werror") {
        return true;
      }
      if (i+1 >= argc) {
        return true;
      }
      i++;
      arg = argv[i];
      if (arg != "on" && arg != "off") {
        return true;
      }
    } else {
      return true;
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

void checkJavaVersion(int major, int minor)
{
  string output;
  if (runAndCaptureStdout("java -version 2>&1", output) != 0) {
    fprintf(stderr, "xta: error: java not available\n");
    exit(1);
  }

  int javaMajorVersion = 0;
  int javaMinorVersion = 0;
  int offset = getInt(output.c_str(), 14, javaMajorVersion); // Start after java version "
  getInt(output.c_str(), offset + 1, javaMinorVersion); // Skip '.'
  
  if ((javaMajorVersion < major) || (javaMinorVersion < minor)) {
    fprintf(stderr, "xta: error: java version must be %d.%d or newer\n", major, minor);
    exit(1);
  }
}

int getInt(const char *str, int offset, int &number)
{
  int len = strlen(str);
  
  if (offset >= len) {
    return offset;
  }

  char buf[BUFFER_SIZE];
  const char *ptr = str + offset;
  
  int i = 0;
  while ((*ptr >= '0') && (*ptr <= '9') &&
         (i < BUFFER_SIZE) && (offset + i < len)) {
    buf[i] = *ptr;
    i++;
    ptr++;
  }
  buf[i] = '\0';
  number = strtoul(buf, NULL, 0);
  return offset + i;
}
