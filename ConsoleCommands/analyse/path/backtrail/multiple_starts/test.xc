#include <xs1.h>

timer Timer;

int f() {
    int time;

    #pragma xta endpoint "start"
	Timer :> time;
    return time;
}

int func(unsigned int delay) {
    int time;

    #pragma xta call "f1"
    time = f();
    #pragma xta call "f2"
    time = f();

    #pragma xta endpoint "stop"
	Timer when timerafter (time + delay) :> unsigned int tmp;
}

int main() {
    func(10);
    return 0;
}

