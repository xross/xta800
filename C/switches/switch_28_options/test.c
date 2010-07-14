int test (int x){
    int y = 0;
    switch (x){
    case 0:
        y=00;
        break;
    case 1:
        y=11;
        break;
    case 2:
        y=22;
        break;
    case 3:
        y=33;
        break;
    case 4:
        y=44;
        break;
    case 5:
        y=55;
        break;
    case 6:
        y=66;
        break;
    case 7:
        y=77;
        break;
    case 8:
        y=88;
        break;
    case 9:
        y=99;
        break;
    case 10:
        y=10;
        break;
    case 11:
        y=11;
        break;
    case 12:
        y=12;
        break;
    case 13:
        y=13;
        break;
    case 14:
        y=14;
        break;
    case 15:
        y=15;
        break;
    case 16:
        y=16;
        break;
    case 17:
        y=17;
        break;
    case 18:
        y=18;
        break;
    case 19:
        y=19;
        break;
    case 20:
        y=20;
        break;
    case 21:
        y=21;
        break;
    case 22:
        y=22;
        break;
    case 23:
        y=23;
        break;
    case 24:
        y=24;
        break;
    case 25:
        y=25;
        break;
    case 26:
        y=26;
        break;
    case 27:
        y=27;
        break;
    }
    return y;
}


int main() {
    int count=0;
    int i;
    for(i=0;i<100;i++){
        count += test(i);
    }
    return count;
}
