int f() {
    int x = 0;
    int y = 0;
    for (x = 0; x < 10; ++x) {
        ++y;
    }
    return y;
}

int main() {
    return f();
}

