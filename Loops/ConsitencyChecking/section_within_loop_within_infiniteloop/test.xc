int main() {
    int x = 0;

    while(1) {
        int i = 0;
        while (i < 100) {
            #pragma xta label "loop" 
          if (i % 2) {           
              #pragma xta label "if" 
              ++x;
          } else {
              ++x;
          }
          i++;
        }
    }
    return x;
}


