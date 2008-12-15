success (){
   return;
}

int main()
{
  int a = 0x15353456;
  int b = 0x25235432;
  if((a | b) == 0x35377476){
      success();
  }
  return;
}

