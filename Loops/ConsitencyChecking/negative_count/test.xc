int main() {
  int x = 0;
  int i = 0;
  while (i < 100) {
    #pragma xta label "loop" 
    i++;
  }
  return x;
}


