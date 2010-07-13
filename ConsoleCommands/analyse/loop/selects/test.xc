#include <xs1.h>

port cled0 = XS1_PORT_1A;
port cled1 = XS1_PORT_1B;
port cled2 = XS1_PORT_1C;
port cledG = XS1_PORT_1D;

#define FLASH_PERIOD 22
#define CYCLE_PERIOD 22

void f(timer tmrF, timer tmrC) {
  int ledOn = 0;
  int ledVal = 0;
  int timeF = 0;
  int timeC = 0;

  while (1) {
    select {
      #pragma xta endpoint "tmrF"
      case tmrF when timerafter(timeF) :> void:
        ledOn = !ledOn;
        cledG <: ledOn;
        timeF += FLASH_PERIOD;
        break;
      #pragma xta endpoint "tmrC"
      case tmrC when timerafter(timeC) :> void:
        cled0 <: ledVal;
        cled1 <: (ledVal >> 4);
        cled2 <: (ledVal >> 8);
        ledVal <<= 1;
        timeC += CYCLE_PERIOD;
        break;
    }
  }
}

int main() {
  timer tmrF;
  timer tmrC;
  f(tmrF, tmrC);
  return 0;
}
