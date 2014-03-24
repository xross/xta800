int test(int loop){
  int res = 0;
  int j = 0;
  int i = 0;
  for(i=0;i<3;i++){    
    for(j=0;j<loop;j++){
      #pragma xta label "inner_loop"
      asm volatile("" : : "r"(i), "r"(j));
      res += j;
    }
  }
  return res;
}

int main(){
    return test(6);
}
