// This test checks that floating loop iterations from the outer loop
// get assigned to the if part of the inner loop section. This should happen
// because the inner loop iterations have been set such that the worst case
// is through the if and not the else.

int main() {
    int a = 0;
    int b = 0;
    int c = 0;
    while(a < 10) {
        #pragma xta label "outer"
        if (a == 22) {
            while(b < 10) {
                #pragma xta label "inner"
                ++b;    
            } 
        } else {
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
            c = 2;
        }
        ++a;
    }
    return c;
}

