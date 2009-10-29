#include <xs1.h>

int main(void) {
    int time;
    timer t1;
    
    #pragma xta endpoint "start"
    t1 :> time;

    #pragma xta endpoint "stop"
    t1 when timerafter(time + 300) :> time;

    return 0;
}
