#include <xs1.h>
#include <platform.h>

int a() {
    return 0;
}
int b() {
    return 0;
}
int c() {
    return 0;
}
int d() {
    return 0;
}

int main() {
    par {
        on stdcore[0] : a();
        on stdcore[1] : b();
        on stdcore[2] : c();
        on stdcore[3] : d();
    }
    return 0;
}

