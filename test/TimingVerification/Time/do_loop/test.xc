int test(){
    int i=0;
    int j=0;
    do {
        j=j+i;
        j=j*j;
        i++;
    } while(i<20);
    return j;
}


int main(){
    return test();
}
