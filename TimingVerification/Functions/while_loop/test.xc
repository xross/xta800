int test(){
    int i=0;
    int j=0;
    while(i<20){
        j=j+i;
        j=j*j;
        i++;
    }
    return j;
}


int main(){
    if (test())
      return 0;
    else
      return 1;
}
