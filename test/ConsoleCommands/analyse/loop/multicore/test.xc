#include <platform.h>

void test() {
    int i;
    while(i < 10) {
        #pragma xta label "loop"
        ++i;
   } 
}

int main() {
    par {
        on tile[0] : test();
        on tile[1] : test();
        on tile[2] : test();
        on tile[3] : test();
    }
    return 0;
}


