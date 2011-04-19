void a() {
}

void b() {
}

int f() {
    int i = 0;
    while (i < 10) {
        ++i;
    }
    return 0;
}

int g() {
    f();
    a();
}

int h() {
    f();
    b();
}

int main(void) {
    g();
    h();
    return 0;
}
