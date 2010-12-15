#include <platform.h>

void test() {
    int i = 0;
    while(i < 10) {
        #pragma xta label "loop"
        ++i;
   } 
}

int main() {
    par {
        on stdcore[0] : test();
        on stdcore[1] : test();
        on stdcore[2] : test();
        on stdcore[3] : test();
    }
    return 0;
}


