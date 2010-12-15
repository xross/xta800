int main() {
    int b = 0;
    int a = 22;
    int i = 0;
    int j = 0;

    while (j < 10) {
        #pragma xta label "outer"
        b = 0;

        while (i < 10) {
            #pragma xta label "inner"
            if (a == 22) {
                b = 3;
                ++b;
            } else {
                #pragma xta label "else"
                b = 4;

                if (a == 22) {
                    #pragma xta label "inner_if"
                    b = 3;
                    ++b;
                } else {
                    b = 2;
                }
            }
            ++i;
        }
        ++j;
    }
    return b;
}

