#include <xs1.h>
#include <platform.h>

#pragma xta command "echo test..."

void core0();
void core1();
void core2();
void core3();

int main() {
  par {
    on tile[0] : core0();
    on tile[1] : core1();
    on tile[2] : core2();
    on tile[3] : core3();
  }
  return 0;
}

