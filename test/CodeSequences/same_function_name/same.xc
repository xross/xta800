static void f (){
  return;
}

int g (int i){
  f();
  return 4+i;
}
