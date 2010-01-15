int test(){
    int i=0;
    int k=0;
    while(i<5){
         int j=0;
         while(j<5){
            k= 1+ k*k;
            j++;
        }
        i++;
    }
    return k;
}


int main(){
    return test();
}
