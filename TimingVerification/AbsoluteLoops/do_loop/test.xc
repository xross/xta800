int test(int loop){
  int res = 0;
  int j = 0;
  int i = 0;
  do {    
    do {
      #pragma xta label "inner_loop"
      res += j;
        j++;
    } while(j<loop);
    i++;
  } while(i<3);
  return res;
}

int main(){
    return test(6);
}
