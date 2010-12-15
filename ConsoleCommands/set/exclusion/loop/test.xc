int main() {
    int a = 0;
    for (int i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        ++a;
    }
    return 0;
}

