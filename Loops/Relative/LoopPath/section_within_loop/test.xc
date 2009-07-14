void f();

int main() {
    int a = 22;
    int b = 23;
    int c = 0;
    while(c < 10) {
        #pragma xta label "loop"
        f();
    }
    return b;
}

