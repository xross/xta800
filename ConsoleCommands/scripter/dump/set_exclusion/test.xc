
int g(int a) {
  if (a == 1) {
    #pragma xta label "x"
    return 10;
  } else {
    #pragma xta label "y"
    return a*3;
  }
}

int main(void) {
    return g(100);
}

