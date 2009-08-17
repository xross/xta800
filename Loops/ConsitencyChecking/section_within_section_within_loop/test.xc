int main() {
    int x = 0;
    int i = 0;
    while (i < 100) {
        #pragma xta label "loop" 
      if (i % 2) {           
          #pragma xta label "if" 
          ++x;
          if (i % 4) {           
              #pragma xta label "inner_if" 
              ++x; 
          } else {
              ++x;
          }
      } else {
          ++x;
      }
      i++;
    }
    return x;
}


