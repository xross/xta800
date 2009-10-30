#include <xs1.h>

in port pIn = XS1_PORT_1A;

int f() {
    int i = 0;
    while (i < 10) {
        #pragma xta label "l"
        if (i == 0) {
            #pragma xta label "lp"
            ++i;
        }
        ++i;
    }
    return 0;
}

int g() {
    int time;
    timer t;

    #pragma xta endpoint "start"
    t :> time;
    f();
    #pragma xta endpoint "stop"
    t when timerafter(time + 300) :> time;
}

int h() {
  #pragma xta endpoint "inst"
  pIn :> int x;
  return 0;
}
 
int main(void) {
    h();
    g();
    return 0;
}

