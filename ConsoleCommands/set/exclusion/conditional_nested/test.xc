int foo(int a) {
    if (a < 10) {
        #pragma xta label "foo_if"
        a += 24;
    } else {
        #pragma xta label "foo_else"
        a += 10;
    }
    return a;
}

int bar(int a) {
    if (a < 10) {
        #pragma xta label "bar_if"
        a += 32;
    } else {
        #pragma xta label "bar_else"
        a += 20;
    }
    return a;
}

int main() {
    int a = 8;
    if (a < 4) {
        #pragma xta label "if"
        a += foo(a * 2);
    } else {
        #pragma xta label "else"
        a += bar(a + 6);
    }
    return 0;
} 
