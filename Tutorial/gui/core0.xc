/*
 * A simple program to explain some of the basic concepts of the XTA tool
 *
 */
#include <xs1.h>
#include <platform.h>

on stdcore[0] : buffered in port:32 dataPort    = XS1_PORT_8A;
on stdcore[0] :          in port    controlPort = XS1_PORT_8B;

unsigned int readWord() {
  unsigned int data;
  unsigned int control;
  
  controlPort :> control;
  data = partin(dataPort, 24);

  return (control << 24 | data >> 8);
}

int inputData(chanend c) {
  start_streaming_master(c);
  
  while(1) {
    unsigned int data = readWord();
    outuint(c, data);
  }
  return 0;
}
