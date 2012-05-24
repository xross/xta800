#include <print.h>

int main() {
    int a = 10;
    int b = 0;
    if (a == 10) {
        b = 1;
    } else {
        #pragma xta label "removed"
        b = 2;
    }
    return b;
}

  
