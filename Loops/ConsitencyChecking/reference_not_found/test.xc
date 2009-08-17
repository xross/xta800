void a() {
  int i = 0;
  while (i < 100) {
    #pragma xta label "loop_a" 
    i++;
  }
}

void b() {
  int i = 0;
  while (i < 100) {
    #pragma xta label "loop_b" 
    i++;
  }
}

int main() {
  a();
  b();
  return 0;
}


