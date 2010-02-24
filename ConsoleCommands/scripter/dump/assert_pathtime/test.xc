#include <xs1.h>

void f() {
    int time;
    timer t;
    #pragma xta endpoint "start"
    #pragma xta endpoint "start"
    #pragma xta endpoint "start"
    t :> time;
    #pragma xta endpoint "stop"
    #pragma xta endpoint "stop"
    #pragma xta endpoint "stop"
    t when timerafter(time + 300) :> time;
}

void g() {
    int time;
    timer t;
    #pragma xta endpoint "start1"
    #pragma xta endpoint "start1"
    #pragma xta endpoint "start1"
    t :> time;
    #pragma xta endpoint "stop1"
    #pragma xta endpoint "stop1"
    #pragma xta endpoint "stop1"
    t when timerafter(time + 300) :> time;
}

void h() {
    int time;
    timer t;
    #pragma xta endpoint "start2"
    #pragma xta endpoint "start2"
    #pragma xta endpoint "start2"
    t :> time;
    #pragma xta endpoint "stop2"
    #pragma xta endpoint "stop2"
    #pragma xta endpoint "stop2"
    t when timerafter(time + 300) :> time;
}

void a() {
  f();
  g(); 
}

void b() {
  f();
  h(); 
}

int main(void) {
    a();
    b();
    return 0;
}
