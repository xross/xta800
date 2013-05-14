int test(){
    int j=0;
    for(int i=0;i<20;i++){
        j=j+i;
        j=j*j;
    }
    return j;
}


int main(){
    if (test())
      return 0;
    else
      return 1;
}
