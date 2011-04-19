#include <xs1.h>

int f(timer t) {
    int time;
    int i = 0;
    t :> time;
    while (i < 10) {
        ++i;
    }
    return time;
}

int main(void) {
    timer t;
    f(t);
    return 0;
}
