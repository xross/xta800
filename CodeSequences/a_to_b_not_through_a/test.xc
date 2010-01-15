int foo(){
    return 1;
}

int bar(){
    return 200;
}

int end(){
    return 200;
}


int test()
{ 
    int x=1;
    while ( x > 0) {
        x += foo();
        if(x == 10){
            x-=bar();
        }
    } 
    return end();
}

int main(){
  return test();
}
