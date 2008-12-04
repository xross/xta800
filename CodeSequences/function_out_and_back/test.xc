

int foo(){
    int x=0;
    return x;
}

int bar(){
    int x;
    while(1){
        x = foo();
        x = foo();
        x = foo();
        x = foo();
    }
    return x;
}

int main()
{
  return bar();
}

