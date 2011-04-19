#include <xs1.h>
#include <print.h>
#include "func.h"

select run1(timer t1, int &time1) {
    #pragma xta endpoint "case1"
    case t1 when timerafter(time1) :> void: {
        printintln(1);
        time1 += PERIOD1;
        break;
    }
}

select run2(timer t2, int &time2) {
    #pragma xta endpoint "case2"
    case t2 when timerafter(time2) :> void: {
        printintln(2);
        time2 += PERIOD2;
        break;
    }
}
