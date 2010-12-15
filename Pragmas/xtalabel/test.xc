int func1() {
    return 1;
}

int func2() {
    return 2;
}

int main() {
    int x = 0;
    if (x == 0) {
        #pragma xta label "label1" 
        x = func1();

    } else {
        #pragma xta label "label2" 
        x = func2();
    }
    return x;
}

