int f(int loopCount) {
    int i = 0;
    int j = 0;
    while (i < loopCount) {
        if (i == 0) {
            #pragma xta label "l"
            ++j;
        }
        ++i;
    }
    return 0;
}

int main() {
    f(10);
    return 0;
}
