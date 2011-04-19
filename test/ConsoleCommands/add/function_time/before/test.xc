#include <xs1.h>

int f() {
    int i = 0;
    while (i < 10) {
        ++i;
    }
    return 0;
}

int g() {
    f();
    f();
}

int main(void) {
    g();
    return 0;
}
