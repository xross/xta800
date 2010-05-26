#include <platform.h>
#include <print.h>

void func();

int main() {
    par {
        on stdcore[0] : { func(); }
        on stdcore[1] : { printstr("hello1\n"); }
        on stdcore[2] : { printstr("hello2\n"); }
        on stdcore[3] : { printstr("hello3\n"); }
    }
    return 0;
}

