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
        y=1010;
        break;
    case 11:
        y=1111;
        break;
    case 12:
        y=1212;
        break;
    case 13:
        y=1313;
        break;
    case 14:
        y=1414;
        break;
    case 15:
        y=1515;
        break;
    case 16:
        y=1616;
        break;
    case 17:
        y=1717;
        break;
    case 18:
        y=1818;
        break;
    case 19:
        y=1919;
        break;
    case 20:
        y=2020;
        break;
    case 21:
        y=2121;
        break;
    case 22:
        y=2222;
        break;
    case 23:
        y=2323;
        break;
    case 24:
        y=2424;
        break;
    case 25:
        y=2525;
        break;
    case 26:
        y=2626;
        break;
    case 27:
        y=2727;
        break;
    case 28:
        y=2828;
        break;
    case 29:
        y=2929;
        break;
    case 30:
        y=3030;
        break;
    case 31:
        y=3131;
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
