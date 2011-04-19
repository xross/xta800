int main() {
    int i = 0;
    int j = 0;
    int k = 0;
    while (i < 5) {
        #pragma xta label "outer"
        ++k; 
        while(j < 5) {
            #pragma xta label "inner"
            ++j;    
        }
        ++i; 
    } 
    return 0;
}

