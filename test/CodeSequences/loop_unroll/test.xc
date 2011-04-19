int foo(int j){
  int i;
  #pragma loop unroll
  for(i=0;i<20;i++){
    j+= (j ^ 0x345) * j;
    if((j & 1) == 0){
      #pragma xta label "exclusion_path"
      j |= i ^ 55;
    } else {
      #pragma xta label "retain_path"
      j += i + 44;
    }
  }
  return j;
}

int main(){
  return foo(0x67);
}
