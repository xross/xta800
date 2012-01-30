int bar(){
    return 200;
}

int end(){
    return 200;
}
int x ;
int test()
{ 
    
    while ( x > 0) {
        #pragma xta call "call_bar"
        x -= bar();
    } 
    #pragma xta call "call_end"
    return end();
}

int main(){
  return test();
}
