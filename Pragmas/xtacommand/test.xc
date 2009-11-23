#pragma xta command "echo function_a..."
#pragma xta command "analyse function function_a"
#pragma xta command "echo function_b..."
#pragma xta command "analyse function function_b"
#pragma xta command "I am an invalid command string"

extern int function_c();
extern int function_d();

int function_a() {
    return 0;
}

int function_b() {
    return 1;
}

int main() {
    function_a();
    function_b();
    function_c();
    function_d();
    return 0;
}

