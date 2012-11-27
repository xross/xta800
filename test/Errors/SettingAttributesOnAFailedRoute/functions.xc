#include <xs1.h>

port p = XS1_PORT_1A;

int h() {
    return 0;
}

int f(int n) {
    int i;
    int j = 0;

    #pragma xta endpoint "ep1"
    p <: 1;

    for (i = 0; i < n; ++i) {
        #pragma xta label "lp1"

        if (i == 10) {
            #pragma xta label "ex1"
            j += 2;

        } else {
            j += i;
        }
    }

    h();

    #pragma xta endpoint "ep2"
    p <: 1;
    #pragma xta endpoint "ep3"
    p <: 1;

    #pragma xta endpoint "ep4"
    p <: 1;

    return j;
}

int g() {
    return 10;
}

