#include <platform.h>

extern void func();

void dummy() {
}

int main() {
  par {
     on tile[0] : func();
     on tile[1] : dummy();
     on tile[2] : dummy();
     on tile[3] : dummy();
  }
  return 0;
}
