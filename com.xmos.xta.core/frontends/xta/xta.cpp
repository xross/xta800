/*
 * Copyright XMOS Limited - 2008
 */

#include <stdlib.h>
#include <stdio.h>
#include <iostream>
#include <string>
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

#define CONSOLE_BASIC "\"--console-basic\""

int main(int argc, char **argv)
{
  const char *jarName = "xta.jar";
  
  string jar;
  if (findJarInPath(jar, jarName))
  {
    string jvmArgString = "";
    const char *jvmArgs = getenv("JVM_ARGS");
    if (jvmArgs != NULL) 
        jvmArgString = jvmArgs;
    
    string args;
    getArgString(args, argc, argv);

    if (needToRun(argc, argv)) {
      checkJavaVersion(1, 5);
     
      string consoleStr = "";
      size_t pos = args.find(CONSOLE_BASIC);
      if (pos != string::npos) {
          args.erase(pos, strlen(CONSOLE_BASIC));           
          consoleStr = "-DConsole=Basic";
      }
 
      string command = "java " + jvmArgString + " " + consoleStr + " -jar \"" + jar + "\"" + args;
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
      if (arg != "Terror" && arg != "Werror" && arg != "Ewarning") {
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

    } else if (arg == "--console-basic") {

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
    // Let the XTA be called so that the error message is consistent
    return true;
  }
  if (xbHandler->error_number != XB_NOERROR) {
    // Let the XTA be called so that the error message is consistent
    return true;
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

  // Truncate, mainly for the benefit of squish which wraps the JVM
  size_t start = output.find("java version \"");
  output = output.substr(start);

  int javaMajorVersion = 0;
  int javaMinorVersion = 0;
  int offset = getInt(output.c_str(), 14, javaMajorVersion); // Start after java version "
  getInt(output.c_str(), offset + 1, javaMinorVersion); // Skip '.'
  
  if ((javaMajorVersion < major) || (javaMinorVersion < minor)) {
    fprintf(stderr, "xta: error: java version must be %d.%d or newer but found %d.%d\n", major, minor, javaMajorVersion, javaMinorVersion);
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
