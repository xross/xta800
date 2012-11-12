#include <xs1.h>

port p = XS1_PORT_1A;

int main() {
    int a = 10;
    int b = 0;
    if (a == 10) {
        b = 1;
    } else {
        #pragma xta endpoint "removed1"
        p <: 1;
        #pragma xta endpoint "removed2"
        p <: 1;
    }
    return b;
}

  
