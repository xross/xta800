#include <xs1.h>

in port p1a = XS1_PORT_1A;
in port p1b = XS1_PORT_1B;

int func() {
    int x = 0;

    #pragma xta endpoint "input_1A" 
    p1a :> x;
    p1a :> x;
    
    #pragma xta endpoint "input_1B" 
    p1b :> x;
    p1b :> x;

    return x;
}
