#include <xs1.h>

#pragma xta command "add exclusion else"
#pragma xta command "add loop loop 10"
#pragma xta command "add looppath loop_path 5"
#pragma xta command "add loop loop_inner 50"
#pragma xta command "add loopscope loop_inner a"
#pragma xta command "add instructiontime inst 100.0 ns"
#pragma xta command "add functiontime f 800.0 ns"
#pragma xta command "add pathtime start stop 2.0 us"

int f() {
    int i = 0;
    int j = 0;

    if (i <10) {
        #pragma xta label "if"
        j = 90;
    } else {
        #pragma xta label "else"
        j = 89;
    }
    return j;
}

int main() {    
    int res = 0;
    int time;
    timer t;

    #pragma xta endpoint "inst"
    t :> time;

    #pragma xta endpoint "start"
    t :> time;

    #pragma xta endpoint "stop"
    t :> time;

    for (int i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        if (i & 0x1) {
            #pragma xta label "loop_path"
            res += 1;

            for (int i = 0; i < 10; ++i) {
                #pragma xta label "loop_inner"
                res += 1;
            }
        }
    }

    f();

    return 0;
}

