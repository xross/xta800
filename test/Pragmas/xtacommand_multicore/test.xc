#include <xs1.h>
#include <platform.h>

#pragma xta command "echo test..."

void core0();
void core1();
void core2();
void core3();

int main() {
  par {
    on stdcore[0] : core0();
    on stdcore[1] : core1();
    on stdcore[2] : core2();
    on stdcore[3] : core3();
  }
  return 0;
}

