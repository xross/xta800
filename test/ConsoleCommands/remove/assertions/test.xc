#include <xs1.h>

in port pIn = XS1_PORT_1A;

int f() {
    int i = 0;
    while (i < 10) {
        #pragma xta label "l1"
        if (i == 0) {
            #pragma xta label "lp1"
            ++i;
        }
        ++i;
    }
    while (i < 10) {
        #pragma xta label "l2"
        if (i == 0) {
            #pragma xta label "lp2"
            ++i;
        }
        ++i;
    }
    return 0;
}

int g() {
    int time;
    timer t;

    #pragma xta endpoint "start1"
    t :> time;
    f();
    #pragma xta endpoint "stop1"
    t when timerafter(time + 300) :> time;

    #pragma xta endpoint "start2"
    t :> time;
    f();
    #pragma xta endpoint "stop2"
    t when timerafter(time + 300) :> time;
}

int h() {
  #pragma xta endpoint "inst1"
  pIn :> int x;
  #pragma xta endpoint "inst2"
  pIn :> int x;
  return 0;
}
 
int main(void) {
    h();
    g();
    return 0;
}

