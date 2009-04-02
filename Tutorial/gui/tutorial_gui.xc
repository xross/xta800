/*
 * A simple program to explain some of the basic concepts of the XTA tool
 *
 */
#include <platform.h>

int inputData(chanend c);
int processData(chanend c);

int main() {
  chan c;
  par
  {
    on stdcore[0] : inputData(c);
    on stdcore[1] : processData(c);
  }
  return 0;
}
