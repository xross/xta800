#include <xs1.h>

in port p1 = XS1_PORT_8A;
in port p2 = XS1_PORT_8B;

int main() {
    int x = 0;

    #pragma xta endpoint "input1" 
    p1 :> x;

    #pragma xta endpoint "input2" 
    p2 :> x;

    return x;
}

