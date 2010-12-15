int f(int loopCount) {
    int i = 0;
    int j = 0;
    int k = 0;
    while (j < loopCount) {
        #pragma xta label "outer"
        ++k;
        while (i < loopCount) {
            #pragma xta label "inner"
            ++i;
        }
        ++j;
    }
    return 0;
}

int main() {
    f(10);
    return 0;
}
