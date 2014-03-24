int test(int loop){
    int res = 0;

  int i = 0;
  while (i < 3) 
  {
    int j = 0;
    while (j < loop) 
    {
      #pragma xta label "inner_loop"
      asm volatile("" : : "r"(i), "r"(j));
      res += j;
      ++j;
    }
    ++i;
  }
  return res;
}

int main(){
    return test(6);
}
