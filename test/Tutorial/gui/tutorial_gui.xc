/*
 * A simple program to explain some of the basic concepts of the XTA tool
 *
 */
#include <platform.h>

int inputData(streaming chanend c);
int processData(streaming chanend c);

int main() {
  streaming chan c;
  par
  {
    on tile[0] : inputData(c);
    on tile[1] : processData(c);
  }
  return 0;
}
