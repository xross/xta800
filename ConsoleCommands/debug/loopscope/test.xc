int main() {    
    int j = 0;
    for (int i = 0; i < 3; ++i) {
        #pragma xta label "outer"
        for (int i = 0; i < 5; ++i) {
            #pragma xta label "inner"
            ++j;
        }
    }
    return 0;
}

