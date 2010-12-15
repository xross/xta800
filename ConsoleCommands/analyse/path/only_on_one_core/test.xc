#include <platform.h>

extern void func();

void dummy() {
}

int main() {
  par {
     on stdcore[0] : func();
     on stdcore[1] : dummy();
     on stdcore[2] : dummy();
     on stdcore[3] : dummy();
  }
  return 0;
}
