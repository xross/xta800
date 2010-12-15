int f(int loopCount) {
    int i = 0;
    while (i < loopCount) {
        #pragma xta label "l"
        ++i;
    }
    return 0;
}

int main() {
    #pragma xta call "fl"
    f(10);
    #pragma xta call "f2"
    f(10);
    return 0;
}
