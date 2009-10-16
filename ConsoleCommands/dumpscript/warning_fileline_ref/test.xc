#include <xs1.h>

port p = XS1_PORT_1A;

void f() {}

int main() {    
    int res = 0;

    // endpoint ref
    for (int i = 0; i < 10; ++i) {
        p <: 1;
    }

    // srclabel ref 
    for (int i = 0; i < 10; ++i) {
        res += 1;
    }

    // call ref
    for (int i = 0; i < 10; ++i) {
        f();
    }
    return 0;
}

