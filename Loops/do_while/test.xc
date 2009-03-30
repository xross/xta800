int test(int x) {
  int res = 0;
  int i = 0;
  do {
    #pragma xta label "label1" 
    res += i;
    i++;
  } while (i < x);
  return res;
}

int main() {
  return test(100);
}

