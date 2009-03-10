void test(int x) {
  switch (x) {
  case 1:
    x = 3;
    break;
  case 2:
    x = 6;
    break;
  case 3:
    x = 9;
    break;
  case 4:
    x = 12;
    break;
  }
}

int main() {
  test(1);
  return 0;
}
