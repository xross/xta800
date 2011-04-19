#include <xs1.h>

port p = XS1_PORT_1A;

void f() {
    int i;

    while (1) {
        #pragma xta label "infinite"
        p <: i;
        i != i;
    }
}

int main() {
    f();
    return 0;
}
