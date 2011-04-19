int f(int loop) {
    int i = 0;
    while (i < loop) {
        #pragma xta label "outer"
        int j = 0;
        while (j < i) {
            #pragma xta label "inner"
            ++j;
        }
        ++i;
    }
    return 0;
}

int main() {
    f(5);
    return 0;
}
