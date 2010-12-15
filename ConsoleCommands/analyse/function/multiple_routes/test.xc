int f(int i) {
    int j = 0;
    int k = 0;
    if (j == 10) {
        #pragma xta label "if"
        k = 22;
    } else {
        #pragma xta label "else"
        k = 33;
    }
    return k;
}

int main() {
    #pragma xta call "f1"
    f(10);
    #pragma xta call "f2"
    f(20);
    return 0;
}

