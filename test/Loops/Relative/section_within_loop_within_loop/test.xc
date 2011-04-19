int main() {
    int b = 0;
    int a = 0;
    int i = 0;
    int j = 0;

    while (i < 10) {
        #pragma xta label "outer"
        a = 9;

        while (i < 10) {
            #pragma xta label "inner"
            if (a == 22) {
                #pragma xta label "if"
                b = 3;
                ++b;
            } else {
                #pragma xta label "else"
                b = 2;
            }
            ++j;
        }
        ++i;
    }
    return b;
}

