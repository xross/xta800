int f(int i) {
    int j = 0;
    while(j < 5) {
        #pragma xta label "inner"
        ++j;    
    }
    return i;
}

int main() {
    int i = 0;
    int j = 0;
    while (f(i) < 5) {
        #pragma xta label "outer"
        ++i; 
    } 
    return 0;
}

