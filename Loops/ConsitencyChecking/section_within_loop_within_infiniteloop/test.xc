#include <xs1.h>

port p = XS1_PORT_1A;

int f(int a, port p) {
    int x = 0;

    while(1) {
        int i = 0;
        while (i < 100) {
            #pragma xta label "loop" 
            if (a == i) {           
                #pragma xta label "if" 
                x+=3;
                p <: 1;

            } else {
                ++x;
                p <: 0;
            }
            i++;
        }
    }
    return x;
}

int main() {
    return f(9, p);
}

