
extern int bar();

int b;
int test() {
  int a;
  for(a=0;a<500;a++){
    b+=bar();
  }
  return b;
}

int main() {
  return test();
}

