/*
 * Copyright XMOS Limited - 2008
 */

#include <stdlib.h>
#include <stdio.h>

#include "frontend.h"

using namespace std;

int main(int argc, char **argv)
{
  string jar;

  const char *jarName = "xta.jar";
  if (findArg("-g", argc, argv))
    jarName = "xtaswing.jar";

  
  if (findJarInPath(jar, jarName))
  {
    string jvmArgString = "";
    const char *jvmArgs = getenv("JVM_ARGS");
    if (jvmArgs != NULL) 
        jvmArgString = jvmArgs;
    
    string args;
    getArgString(args, argc, argv);

    string command = "java " + jvmArgString + " -jar " + jar + args;
    int returnCode = run(command.c_str());
    return returnCode;
  }
  else
  {
    fprintf(stderr, "Cannot find tool '%s'\n", jarName);
    return 1;
  }
}

