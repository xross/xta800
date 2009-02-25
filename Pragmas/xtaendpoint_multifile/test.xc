#include <xs1.h>

int func();

in port p8a = XS1_PORT_8A;
in port p8b = XS1_PORT_8B;

int main() {
    int x = 0;

    #pragma xta endpoint "input_8A" 
    p8a :> x;
    p8a :> x;
    
    #pragma xta endpoint "input_8B" 
    p8b :> x;
    p8b :> x;

    func();
    return x;
}

