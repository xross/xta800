int function_a() {
    return 0;
}

int function_b() {
    return 1;
}

int function_c() {
    return 2;
}

int function_d() {
    return 3;
}

int main() {
    #pragma xta call "call_function_a"
    function_a();
    function_b();

    #pragma xta call "call_function_c"
    function_c();
    function_d();

    return 0;
}

