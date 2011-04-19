int foo() {
    return 4;
}

int bar() {
    return 6;
}

int main() {
    int a = 8;
    if (a < 4) {
        #pragma xta label "if"
        a += foo();
    } else {
        a += bar();
    }
    return 0;
} 
