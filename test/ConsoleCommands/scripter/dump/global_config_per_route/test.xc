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
        on tile[0] : a();
        on tile[1] : b();
        on tile[2] : c();
        on tile[3] : d();
    }
    return 0;
}

