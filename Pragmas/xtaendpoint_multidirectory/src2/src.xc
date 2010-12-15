#include <xs1.h>

in port p1c = XS1_PORT_1C;
in port p1d = XS1_PORT_1D;

int func2() {
    int x = 0;

    #pragma xta endpoint "input_1C" 
    p1c :> x;
    p1c :> x;
    
    #pragma xta endpoint "input_1D" 
    p1d :> x;
    p1d :> x;

    return x;
}
