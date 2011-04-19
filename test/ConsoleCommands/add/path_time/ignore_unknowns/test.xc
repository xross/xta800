#include <xs1.h>

int f(timer t) {
    int i = 0;
    while (i < 10) {
        ++i;
    }
    return 0;
}

int main(void) {
    int time;
    timer t;
    
    #pragma xta endpoint "start"
    t :> time;
    f(t);
    #pragma xta endpoint "stop"
    t :> time;

    return 0;
}
