#include <xs1.h>

port p = XS1_PORT_1A;

int main() {
    int i = 0;

    #pragma xta endpoint "start"
    p :> i;

    i = 10;

    #pragma xta endpoint "start"
    p :> i;

    i = 20;

    #pragma xta endpoint "stop"
    p :> i;

    return 0;
}

