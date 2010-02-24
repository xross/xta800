extern unsigned char a,x,y,s,p;
extern unsigned short pc;

void cpuParse(void);
void push(unsigned char val);

// Trying to prevent inlining of cpuParse
void cpuJSR(unsigned short npc, unsigned char na)
{  
    a=na;
    x=0;
    y=0;
    p=0;
    s=255;
    pc=npc;
    push(0);
    push(0);

    while (pc > 1)
        cpuParse();
}
