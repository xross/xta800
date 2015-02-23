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
#include "XmosDefines.h"
#include "frontend.h"

using namespace std;

bool needToRun(int argc, char **argv);
bool xeContainsXtaCommands(char *xeName);
string getConsoleStr(string &args);
string getJvmHeapSizeStr(string &args);

#define CONSOLE_BASIC "\"--console-basic\""
#define JVM_HEAP_SIZE "--jvm-heap-size"

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
      checkJavaVersion(1, 6);
     
      string consoleStr = getConsoleStr(args);
      string jvmHeapSizeStr = getJvmHeapSizeStr(args);
 
      string command = "java -Xmx256m " + jvmArgString + " " + 
        consoleStr + " " + jvmHeapSizeStr + " -jar \"" + jar + "\"" + args;

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

string getConsoleStr(string &args) {
  string str = "";
  size_t pos = args.find(CONSOLE_BASIC);
  if (pos != string::npos) {
    str = "-DConsole=Basic";
    args.erase(pos, strlen(CONSOLE_BASIC));           
  }
  return str;
}

string getJvmHeapSizeStr(string &args) {
  string str = "";
  size_t pos = args.find(JVM_HEAP_SIZE);
  if (pos != string::npos) {
    size_t start = args.find('=', pos) + 1;
    size_t end   = args.find('"', pos);
    str = "-Xmx" + args.substr(start, end - start);
    args.erase(pos - 1, end - pos + 2);           
  }
  return str;
}

