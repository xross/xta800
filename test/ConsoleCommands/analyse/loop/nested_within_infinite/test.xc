#include <xs1.h>

port p = XS1_PORT_1A;

void f(int loopCount) {
    int i = 0;
    int j ;

    while (1) {
        while (j < loopCount) {
            #pragma xta label "inner"
            p <: i;
        }
        i != i;
    }
}

int main() {
    f(5);
    return 0;
}

