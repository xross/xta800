int g() {
    #pragma xta label "from_g"
    return 1;
}

int f(int loopCount) {
    int i = 0;
    while (i < loopCount) {
        i += g();
    }
    return 0;
}

int main() {
    f(10);
    return 0;
}
