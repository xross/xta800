int main() {    
    int j = 0;
    for (int i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        if (i == 0) {          
            #pragma xta label "if"
            j += 3;
        } else {
            #pragma xta label "else"
            j += 34;
        }
    }
    return 0;
}

