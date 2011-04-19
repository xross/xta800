#include <xs1.h>

timer Timer;

int func(unsigned int delay) {
	unsigned int time;
    #pragma xta endpoint "start"
	Timer :> time;
    #pragma xta endpoint "stop"
	Timer when timerafter (time + delay) :> unsigned int tmp;
}

int test(unsigned int a) {
    if (a) {
        #pragma xta label "func1" 
        func(10);
    } else {
        #pragma xta label "func2" 
        func(20);
    }
}

int main() {
    int a = 0;
    int b = 0;
    if (a) {
        #pragma xta label "test1" 
        b = 0;
        test(0);
    } else {
        #pragma xta label "test2"
        b = 1;
        test(1); 
    }
    return b;
}

