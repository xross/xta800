int j() {
  return 0;
}

int g() {
  return 0;
}

int h() {
  return 0;
}

int f() {
    #pragma xta call "callptr"
    j();
  return 0;
}

int main() {
    #pragma xta call "callg"
    f();
    #pragma xta call "callh"
    f();
g();
h();
    return 0;
}
