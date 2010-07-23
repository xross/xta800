void f() {
}

void g() {
}

void h() {
    int j = 0;
    for (int i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        ++j;
    }
}

int main() {
    #pragma xta call "callf"
    f();
    #pragma xta call "callg"
    g();
    h();
    return 0;
}

