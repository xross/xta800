#include <stdio.h>

__attribute__((fptrgroup("fptr")))
int g() {
    printf("Hello there\n");
}

int f(__attribute__((fptrgroup("fptr"))) int (*fptr)()) {
    (*fptr)();
}

int main() {
    f(&g);
    return 0;
}

    
