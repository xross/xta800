int fi() { return 10; }
int fe() { return 11; }
int bi() { return 12; }
int be() { return 13; }

int foo(int a) {
    if (a < 10) {
        #pragma xta label "foo_if"
        a += fi();
    } else {
        #pragma xta label "foo_else"
        a += fe();
    }
    return a;
}

int bar(int a) {
    if (a < 10) {
        #pragma xta label "bar_if"
        a += bi();
    } else {
        #pragma xta label "bar_else"
        a += be();
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
