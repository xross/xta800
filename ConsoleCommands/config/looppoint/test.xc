int main() {
    int i = 0;
    while(1) {
        #pragma xta label "loop"
        ++i;
    }
    return 0;
}

