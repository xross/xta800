#include <stdio.h>

int g() {
    printf("Hello there\n");
}

int f(int (*fptr)()) {
    (*fptr)();
}

int main() {
    f(&g);
    return 0;
}

    
