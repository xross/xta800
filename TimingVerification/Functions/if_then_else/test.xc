int test(){
    int i=0;
    int j=0;
    if(i==0){
        i++;
        j=j*i;
    } else {
        i--;
    }
    return j;
}


int main(){
    return test();
}
