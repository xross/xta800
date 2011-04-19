#include <xs1.h>

int f(timer t) {
    int time;
    int i = 0;

    while (i < 10) {
        ++i;
    }
 
    #pragma xta endpoint "stop"
    t when timerafter(time + 300) :> time;

    return 0;
}

int main(void) {
    int time;
    timer t;
    
    #pragma xta endpoint "start"
    t :> time;
    f(t);

    return 0;
}
