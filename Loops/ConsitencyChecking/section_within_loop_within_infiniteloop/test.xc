int main() {
    int x = 0;

    while(1) {
        int i = 0;
        while (i < 100) {
            #pragma xta label "loop" 
          if (i % 9) {           
              #pragma xta label "if" 
              x+=3;
          } else {
              ++x;
          }
          i++;
        }
    }
    return x;
}


