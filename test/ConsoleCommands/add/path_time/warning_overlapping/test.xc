#include <xs1.h>

int f() {
    int i = 0;
    while (i < 10) {
        ++i;
    }
    return 0;
}

int main(void) {
    int time;
    timer t1;
    timer t2;
    
    #pragma xta endpoint "start1"
    t1 :> time;
    #pragma xta endpoint "start2"
    t2 :> time;

    f();

    #pragma xta endpoint "stop1"
    t1 when timerafter(time + 300) :> time;
    #pragma xta endpoint "stop2"
    t2 when timerafter(time + 300) :> time;

    return 0;
}
