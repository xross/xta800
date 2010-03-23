int g() {
  return 0;
}

int f(int i) {
  int j = 0;
  for (j = 0; j < i; ++j) {
    #pragma xta label "loop"
    g();
  }
  return i;
}

int main() {
  f(10);
  return 0;
}

