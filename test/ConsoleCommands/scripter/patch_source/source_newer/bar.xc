#include <xs1.h>
#include <platform.h>

int foo();

out port q = XS1_PORT_1B;

int bar(){
    int x;
    for (int i = 0; i < 10; i++) {
        q<:foo();
        q<:foo();
        q<:foo();
        q<:foo();
    }
    return x;
}
