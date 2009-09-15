int test(int loop1, int loop2){
  int res = 0;
  int j = 0;
  int i = 0;
  int k = 0;
  for(i=0;i<3;i++){    
    for(j=0;j<loop1;j++){
      #pragma xta label "outer_loop"
        for(k=0;k<loop2;k++){
          #pragma xta label "inner_loop"
          res += k;
        }
      res += j;
    }
  }
  return res;
}

int main(){
    return test(6, 6);
}
