int test(int loop){
  int res = 0;
  int i = 0;
  do {    
    int j = i;
    do {
      #pragma xta label "inner_loop"
      asm volatile("" : : "r"(i), "r"(j));
      res += j;
      j++;
    } while (j < loop);
    #pragma xta label "outer_loop"
    i++;
  } while (i < 3);
  return res;
}

int main(){
    return test(6);
}
