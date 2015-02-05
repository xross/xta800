#include <platform.h>
#include <print.h>

void func();

int main() {
    par {
        on tile[0] : { func(); }
        on tile[1] : { printstr("hello1\n"); }
        on tile[2] : { printstr("hello2\n"); }
        on tile[3] : { printstr("hello3\n"); }
    }
    return 0;
}

