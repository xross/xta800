int main() {
    int b = 0;
    int i = 0;
    while (i < 10) {
        #pragma xta label "loop"
        int a = 22;
        int b = 23;
        if (a == 22) {
            b = 3;
            ++b;
        } else {
            if (a == 22) {
                b = 3;
                ++b;
            } else {
                b = 2;
            }
        }
        ++i;
    }
    return b;
}

