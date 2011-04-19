int g() {
    #pragma xta label "from_g"
    return 1;
}

void a(int loopCount) {
    int i = 0;
    while (i < loopCount) {
        i += g();
    }
}

void b(int loopCount) {
    int i = 0;
    while (i < loopCount) {
        i += g();
    }
}

int main() {
    a(5);
    b(10);
    return 0;
}
