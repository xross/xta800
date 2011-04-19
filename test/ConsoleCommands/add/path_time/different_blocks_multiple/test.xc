#include <xs1.h>

int f() {
    return 0;
}

int main(void) {
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

    #pragma xta endpoint "start3"
    t :> time;
    f();
    #pragma xta endpoint "stop3"
    t when timerafter(time + 300) :> time;

    return 0;
}
