void f() {
}

void g() {
}

int main() {
    #pragma xta call "callf"
    f();
    #pragma xta call "callg"
    g();
    return 0;
}

