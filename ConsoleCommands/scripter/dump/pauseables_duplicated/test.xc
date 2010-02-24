#include <xs1.h>

in port pIn = XS1_PORT_1A;

void f() {
    #pragma xta endpoint "inst"
    #pragma xta endpoint "f1,inst"
    #pragma xta endpoint "f1,inst"
    pIn :> int x;
}

void g() {
    #pragma xta call "f1" 
    f();
    #pragma xta call "f2" 
    f();
}
    
int main() {
    g();
    return 0;
}

