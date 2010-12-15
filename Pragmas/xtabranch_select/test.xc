#include <xs1.h>

in port p1 = XS1_PORT_8A;
in port p2 = XS1_PORT_8B;

int main() {
    int x = 0;
    select {
        case p1 :> x:
            break;
        case p2 :> x:
            break;    
    }
    return x;
}

