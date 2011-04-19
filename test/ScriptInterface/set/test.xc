#include <xs1.h>

in port pIn = XS1_PORT_1A;

int f() {
    int i = 0;
    return 0;
}

int functiontime() {
    f();
}

int pathtime() {
    int time;
    timer t;

    #pragma xta endpoint "start"
    t :> time;
    f();
    #pragma xta endpoint "stop"
    t when timerafter(time + 300) :> time;
}

void instructiontime() {
  #pragma xta endpoint "inst"
  pIn :> int x;
}

void exclusion() {
    int i = 0;
    if (i == 1) {
        #pragma xta label "_exclusion"
        f();
    }
}

void loopscope() {
    int i = 0;
    int j = 0;
    int k = 0;
    for (i = 0; i < 5; ++i) {
        #pragma xta label "outer"
        for (i = 0; i < 5; ++i) {
            #pragma xta label "inner"
            ++k;
        }
    }
}

void looppath() {
    int i = 0;
    int j = 0;
    for (i = 0; i < 5; ++i) {
        #pragma xta label "_looppath"
        if (i == 0) {
            #pragma xta label "if"
            j = 1;
        }
    }
}

void loop() {
    int i = 0;
    int j = 0;
    for (i = 0; i < 5; ++i) {
        #pragma xta label "_loop"
         j = 1;
    }
}

int main() {
    loop();
    looppath();
    loopscope();
    functiontime();
    pathtime();
    instructiontime();
    exclusion();
    return 0;
}

