int test(){
    int i=0;
    int j=0;
    int k=0;
    for(i=0;i<5;i++){
        for(j=0;j<5;j++){
            k= 1+ k*k;
        }
    }
    return k;
}


int main(){
    return test();
}
