int test(int x) {
  int res = 0;
  int j = 0;
  do {
    int i = 0;
    while (i < x) {
      #pragma xta label "while" 
      res += i;
      i++;
    }
    #pragma xta label "do" 
    j++;
  } while (j < x);
  return res;
}

int main() {
  return test(100);
}

