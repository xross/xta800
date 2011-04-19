int test(int loop){
  int res = 0;
  int j = 0;
  int i = 0;
  for(i=0;i<3;i++){    
    for(j=0;j<loop;j++){
      #pragma xta label "inner_loop_1"
      res += j;
    }
    for(j=0;j<loop;j++){
      #pragma xta label "inner_loop_2"
      res += j;
    }
    for(j=0;j<loop;j++){
      #pragma xta label "inner_loop_3"
      res += j;
    }
    for(j=0;j<loop;j++){
      #pragma xta label "inner_loop_4"
      res += j;
    }
  }
  return res;
}

int main(){
    return test(6);
}
