static void f (){
  int g = 0;
  g++;
  return;
}

int g (int i){
  f();
  return 4+i;
}
