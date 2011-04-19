#include "func.h"

int main() {
    int count = 0;

    timer t1;
    timer t2;

    int time1;
    int time2;

    t1 :> time1;
    time1 += PERIOD1;

    t2 :> time2;
    time2 += PERIOD2;

    select {
        case run1(t1, time1);
        case run2(t2, time2);
    }
    return 0;
}
