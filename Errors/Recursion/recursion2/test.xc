#include <print.h>

void f(int i) {
    if (i > 0) {
        f(i - 1);
    }    
}

int main() {
    f(10);
    return 0;
}

