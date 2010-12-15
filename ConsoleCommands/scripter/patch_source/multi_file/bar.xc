int foo();

int bar(){
    int x;
    for (int i = 0; i < 10; i++) {
        x = foo();
        x = foo();
        x = foo();
        x = foo();
    }
    return x;
}
