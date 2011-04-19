#include <platform.h>

void f0() {}
void f1() {}
void f2() {}
void f3() {}

int main() {
    par {
    on stdcore[0] : f1();
    on stdcore[1] : f2();
    on stdcore[2] : f2();
    on stdcore[3] : f3();
    }
    return 0;
}

