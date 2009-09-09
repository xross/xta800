void foo(){
    int i;
    i++;
}

int test(){
    int i=0;
    int j=0;
    if(i==0){
        i++;
        foo();
        foo();
        foo();
    } else {
        i--;
    }
    return j;
}


int main(){
    return test();
}
