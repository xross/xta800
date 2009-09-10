int test(){
    int i=0;
    int j=0;
    int k=0;
    int l=0;
    int m=0;
    for(i=0;i<3;i++){
        for(j=0;j<3;j++){
            for(k=0;k<3;k++){
                for(l=0;l<3;l++){
                    m= 1+ m*m;
                }
            }
        }
    }
    return m;
}

int main(){
    return test();
}
