int main() {    
    int res = 0;

    for (int i = 0; i < 10; ++i) {
        #pragma xta label "loop"
        res += 1;
    }

    for (int i = 0; i < 10; ++i) {
        if (i & 0x1) {
            #pragma xta label "loop_path"
            res += 1;
        }
    }

    for (int i = 0; i < 10; ++i) {
        res += 1;
    }

    for (int i = 0; i < 10; ++i) {
        if (i & 0x1) {
            res += 1;
        }
    }

    return 0;
}

