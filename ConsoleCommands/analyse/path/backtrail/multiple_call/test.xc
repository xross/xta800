#include <xs1.h>

timer Timer;

int func(unsigned int delay) {
	unsigned int time;
    #pragma xta endpoint "start"
	Timer :> time;
    #pragma xta endpoint "stop"
	Timer when timerafter (time + delay) :> unsigned int tmp;
}

int test() {
    #pragma xta call "func1" 
    func(10);
    #pragma xta call "func2" 
    func(20);
}

int main() {
    #pragma xta call "test1" 
    test();
    #pragma xta call "test2" 
    test();
    return 0;
}
