int main() {    
    int j = 0;
    for (int i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        ++j;
    }
    return 0;
}

