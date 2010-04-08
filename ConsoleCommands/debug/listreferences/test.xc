void f() {
    int j = 0;
    for (int i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        ++j;
    }
}

int test() {
    #pragma xta call "f1"
    f();
    #pragma xta call "f2"
    f();
}

int main() {    
    #pragma xta call "call1"
    test();
    #pragma xta call "call2"
    test();
    return 0;
}

