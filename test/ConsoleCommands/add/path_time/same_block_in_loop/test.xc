#include <xs1.h>

int f() {
    int i = 0;
    while (i < 10) {
        ++i;
    }
    return 0;
}

int g() {
    int time;
    timer t;
    
    #pragma xta endpoint "outer_start"
    t :> time;

    #pragma xta endpoint "start"
    t :> time;
    #pragma xta endpoint "stop"
    t when timerafter(time + 300) :> time;
    
    #pragma xta endpoint "outer_stop"
    t :> time;
}

int main(void) {
    int i = 0;
    for (i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        g();
    }
    return 0;
}
