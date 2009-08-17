int main() {
  int x = 0;
  int i = 0;
  while (i < 100) {
    #pragma xta label "loop" 
    if (i % 2) {           
      #pragma xta label "looppath1"
      ++x;
    } else {
      #pragma xta label "looppath2"
      ++x;
    }
    i++;
  }
  return x;
}


