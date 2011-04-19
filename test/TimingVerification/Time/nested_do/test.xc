int test(){
    int i=0;
    int j=0;
    int k=0;
    do {
         do {
            k= 1+ k*k;
            j++;
        } while(j<5);
        i++;
    } while(i<5);
    return k;
}


int main(){
    return test();
}
