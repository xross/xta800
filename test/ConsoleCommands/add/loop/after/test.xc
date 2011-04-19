int f(int loopCount) {
    int i = 0;
    while (i < loopCount) {
        #pragma xta label "l"
        ++i;
    }
    return 0;
}

int main() {
    f(10);
    return 0;
}
