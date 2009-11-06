int f(int x, int y) {
    int res = 0;
    for (int i = 0; i < x; ++i) {
        #pragma xta label "outer1"
        for (int j = 0; j < y; ++j) {
            if (j == 0) {
                #pragma xta label "inner1"
                ++res;
            }
        }
    }   
    return res;
}

int g(int x, int y) {
    int res = 0;
    for (int i = 0; i < x; ++i) {
        #pragma xta label "outer2"
        for (int j = 0; j < y; ++j) {
            if (j == 0) {
                #pragma xta label "inner2"
                ++res;
            }
        }
    }   
    return res;
}

int main(void) {
    f(10, 20);
    g(20, 40);
    return 0;
}

