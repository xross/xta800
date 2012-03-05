void noReturn();

//start test0 infinite
void test0(int x){ 
  if(x){          //no_path
    noReturn();   //path
  }               //path
}                 //no_path
//stop

//start test1 infinite
void test1(int x){
  if(x){          //no_path
    noReturn();   //path
  }               //path
                  //no_path
}                 //no_path
//stop
