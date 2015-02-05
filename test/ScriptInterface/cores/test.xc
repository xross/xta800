#include <platform.h>

void f0() {}
void f1() {}
void f2() {}
void f3() {}

int main() {
    par {
    on tile[0] : f1();
    on tile[1] : f2();
    on tile[2] : f2();
    on tile[3] : f3();
    }
    return 0;
}

