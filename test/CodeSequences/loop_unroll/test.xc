extern int g(int);
extern int h(int);


int foo(int j){
  int i;
  #pragma loop unroll
  for(i=0;i<20;i++){
    j+= (j ^ 0x345) * j;
    if((j & 1) == 0){
      #pragma xta label "exclusion_path"
      j |= g(i ^ 55);
    } else {
      #pragma xta label "retain_path"
      j += h(i + 44);
    }
  }
  return j;
}
int x;
int main(){
  return foo(x);
}
