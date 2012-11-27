#include <xs1.h>

port p = XS1_PORT_1A;

int a() {
    return 0;
}

void f() {
    #pragma xta endpoint "start"
    p <: 1;
}

void g(int n) {
    int i;
    int j = 0;

    for (i = 0; i < n; ++i) {
        #pragma xta label "lp1"

        if (i == 10) {
            #pragma xta label "ex1"
            j += 2;

        } else {
            j += i;
        }
    }

    a();

    #pragma xta endpoint "ep1"
    p <: 1;
    #pragma xta endpoint "ep2"
    p <: 1;
}

void h() {
    #pragma xta endpoint "end"
    p <: 1;
}


