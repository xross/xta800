int main() {
  int i = 0;
  do {
    #pragma xta label "loop" 
    i++;
  } while (i < 100);
  return 0;
}


