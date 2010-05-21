void f() {}
void g() {}
void h() {}
void i() {}
void j() {}
void k() {}
void l() {}

int main() {
    #pragma xta call "callf"
    f();
    #pragma xta call "callg"
    g();
    h();
    i();
    j();
    k();
    l();
    return 0;
}

