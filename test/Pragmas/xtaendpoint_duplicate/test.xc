#include <xs1.h>

in port p1 = XS1_PORT_8A;

int main() {
    int x = 0;
    
    #pragma xta endpoint "SAME" 
    #pragma xta endpoint "SAME" 
    p1 :> x;

    #pragma xta endpoint "DIFFERENT" 
    p1 :> x;

    #pragma xta endpoint "DIFFERENT" 
    p1 :> x;

    return x;
}

