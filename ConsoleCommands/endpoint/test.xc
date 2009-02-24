#include <xs1.h>

in port p1 = XS1_PORT_8A;
in port p2 = XS1_PORT_8B;

int main() {
    int x = 0;
    
    p1 :> x;
    p2 :> x;

    return x;
}


