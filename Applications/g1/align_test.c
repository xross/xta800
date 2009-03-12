#include <stdio.h>
#include <memory.h>
#include <assert.h>
#include <stdlib.h>

struct packet
{
  unsigned tag;
  int nbits;
  void *data;
};

struct algorithm_state
{
  int spanning;
  unsigned spanning_sync;
  int shift;
  unsigned residue;
  struct packet *pkts;
  int npkts;
  int npktwords;
  unsigned *pktwords;
};

// Emulate XS1 shift
// Intel machines do shift 32 as shift 0!!
unsigned shl(unsigned x, int n)
{
  if (n >= 32)
    return 0;
  else
    return x << n;
}

// Emulate XS1 shift
// Intel machines do shift 32 as shift 0!!
unsigned shr(unsigned x, int n)
{
  if (n >= 32)
    return 0;
  else
    return x >> n;
}

// Emulate XS1 bitrev
unsigned bitrev(unsigned x)
{
  unsigned y = 0;
  int i;
  for (i = 0; i < 32; i++)
  {
    y |= (((x >> i) & 1) << (31 - i));
  }
  return y;
}

// Emulate XS1 clz
unsigned clz(unsigned x)
{
  int n = 0;
  int i;
  for (i = 31; i >= 0; i--)
  {
    if ((x & (1 << i)) != 0)
      break;
    else
      n++;
  }
  return n;
}

void init(struct algorithm_state *s, struct packet *packets)
{
  s->spanning = 0;
  s->spanning_sync = 0;
  s->shift = 32;
  s->residue = 0;
  s->pkts = packets;
  s->npkts = 0;
  s->npktwords = 0;
  s->pktwords = (unsigned*)(packets[0].data);
  packets[0].tag = 0;
}

void addword(struct algorithm_state *s, unsigned w)
{
  s->pktwords[s->npktwords] = w;
  s->npktwords++;
}

void pktdone(struct algorithm_state *s, unsigned char T)
{
  s->pkts[s->npkts].nbits = s->npktwords * 32;
  s->npkts++;
  s->npktwords = 0;
  s->pktwords = (unsigned*)s->pkts[s->npkts].data;
  s->pkts[s->npkts].tag = T;
}

void next(struct algorithm_state *s, unsigned sdo, unsigned sync)
{
  if (s->spanning > 0)
  {
    unsigned w = shl(sdo, 32 - s->shift) | s->residue;
    unsigned t = shr(shl(sdo, 32 - s->spanning), 32 + s->shift - s->spanning);
    unsigned char T = shl(sync, 8 - s->spanning) | shr(s->spanning_sync, s->spanning + 24);
    s->shift = s->spanning;
    s->residue = shr(sdo, s->spanning);
    s->spanning = 0;
    addword(s, w);
    addword(s, t);
    pktdone(s, T);
  }
  else
  {
    unsigned L = clz(bitrev(sync));
    if (L > 24)
    {
      unsigned w = shl(sdo, 32 - s->shift) | s->residue;
      s->residue = shr(sdo, s->shift);
      if (L < 32)
      {
        s->spanning = L - 24;
        s->spanning_sync = sync;
      }
      addword(s, w);
    }
    else
    {
      unsigned w = shl(sdo, 32 - s->shift) | s->residue;
      unsigned t = shr(shl(sdo, 24 - L), 24 - L + s->shift);
      unsigned char T = shr(sync, L);
      s->shift = 8 + L;
      s->residue = shr(sdo, 8 + L);
      addword(s, w);
      addword(s, t);
      pktdone(s, T);
    }
  }
}

int getbit(const void *data, int n)
{
  const unsigned char *bytes = (const unsigned char*)data;
  unsigned char byte = bytes[n >> 3];
  return ((byte >> (n & 7)) & 1);
}

struct get32bits_state
{
  int npktsdone;
  int pktbitsdone;
  int npackets;
  const struct packet *packets;
};

void get32bits_initstate(struct get32bits_state *s, const struct packet *packets, int npackets)
{
  s->packets = packets;
  s->npackets = npackets;
  s->npktsdone = 0;
  s->pktbitsdone = 0;
}

void get32bits(unsigned *sdoword, unsigned *syncword, struct get32bits_state *s)
{
  unsigned x = 0;
  unsigned y = 0;
  int i;
  for (i = 0; i < 32; i++)
  {
    if (s->npktsdone < s->npackets)
    {
      int pktnbits = s->packets[s->npktsdone].nbits;
      if (s->pktbitsdone >= pktnbits - 8)
      {
        //unsigned char tag = (s->npktsdone < s->npackets - 1) ? s->packets[s->npktsdone + 1].tag : 0;
        unsigned char tag = (s->npktsdone < s->npackets - 1) ? 0xFF : 0;
        int bitnum = s->pktbitsdone - pktnbits + 8;  // 0 1 2 3 4 5 6 7
        int bit = ((tag >> bitnum) & 1);
        y |= (bit << i);
if(tag!=0)printf("%d <- %d [%d]\n", s->pktbitsdone, bitnum, bit);
      }
      int bit = getbit(s->packets[s->npktsdone].data, s->pktbitsdone);
      x |= (bit << i);
      s->pktbitsdone++;
      if (s->pktbitsdone == pktnbits)
      {
        s->npktsdone++;
        s->pktbitsdone = 0;
      }
    }
  }
  *sdoword = x;
  *syncword = y;
}

int main()
{
  const int npackets = 32;
  const int nbits_max = 256;
  const int nbits_min = 32;
  int nbits_all_packets = 0;
  struct packet *input;
  struct packet *output;
  unsigned *sync_words, *sdo_words;
  int i, j, k;
  int nwords;
  struct get32bits_state state;
  struct algorithm_state s;
  int allok;

  srand(55);
  assert(nbits_min >= 8);

  // Generate packets
  input = malloc(sizeof(struct packet) * npackets);
  output = malloc(sizeof(struct packet) * npackets);
  for (i = 0; i < npackets; i++)
  {
    int nbits = rand() % (nbits_max + 1 - nbits_min) + nbits_min;
    int nbytes = nbits / 8 + 1;
    input[i].tag  = ((rand() & 0xF0) | 0x7);
    input[i].nbits = nbits;
    input[i].data = malloc(nbytes + 8);
    memset(input[i].data, 0, nbytes + 8);
    output[i].tag = 0;
    output[i].nbits = nbits;
    output[i].data = malloc(nbytes + 32);
    memset(output[i].data, 0, nbytes + 32);
    for (j = 0; j < nbytes; j++)
    {
      ((unsigned char*)input[i].data)[j] = rand() & 0xFF;
    }
    nbits_all_packets += nbits;
  }

  // Generate sync and sdo words
  nwords = nbits_all_packets / 32 + 2;
  sync_words = malloc(nwords * 4 + 8);
  sdo_words = malloc(nwords * 4 + 8);
  memset(sync_words, 0, nwords * 4);
  memset(sdo_words, 0, nwords * 4);
  get32bits_initstate(&state, input, npackets);
  for (k = 0; k < nwords; k++)
  {
    get32bits(&sdo_words[k], &sync_words[k], &state);
  }

  // Print packets
#if 1
  printf("+ input\n");
  for (i = 0; i < npackets; i++)
  {
    printf("%4d %02X %3d ", i, input[i].tag & 0xFF, input[i].nbits);
    nwords = input[i].nbits / 8 / 4 + 1;
    for (j = 0; j < nwords; j++)
    {
      printf("%08X ", ((const unsigned*)input[i].data)[j]);
    }
    printf("\n");
  }
#endif

  // Print words
#if 1
  printf("+ sync/sdo\n");
  nwords = nbits_all_packets / 32 + 2;
  for (k = 0; k < nwords; k++)
  {
    printf("%08X %08X\n", sync_words[k], sdo_words[k]);
  }
#endif

  // Parse words
  init(&s, output);
  nwords = nbits_all_packets / 32 + 2;
  for (k = 0; k < nwords; k++)
  {
    next(&s, sdo_words[k], sync_words[k]);
  }

  // Print packets from parsed words
#if 1
  printf("+ output\n");
  for (i = 0; i < npackets; i++)
  {
    printf("%4d %02X %3d ", i, output[i].tag & 0xFF, output[i].nbits);
    nwords = output[i].nbits / 8 / 4 + 1;
    for (j = 0; j < nwords; j++)
    {
      printf("%08X ", ((const unsigned*)output[i].data)[j]);
    }
    printf("\n");
  }
#endif

  // Check output packets against input packets
#if 1
  printf("+ check\n");
  allok = 1;
  for (i = 0; i < npackets; i++)
  {
    if (output[i].tag != input[i].tag && i > 0)
    {
      printf("MISMATCH packet %d tag (input:%02X output:%02X)\n", i, input[i].tag, output[i].tag);
      allok = 0;
    }
    for (j = 0; j < input[i].nbits; j++)
    {
      unsigned char inbit = getbit(input[i].data, j);
      unsigned char outbit = getbit(output[i].data, j);
      if (inbit != outbit)
      {
        printf("MISMATCH packet %d bit %d (input:%d output:%d)\n", i, j, inbit, outbit);
        allok = 0;
      }
    }
  }
  if (allok)
  {
    printf("ALL OK\n");
  }
#endif

  free(sdo_words);
  free(sync_words);
  for (i = 0; i < npackets; i++)
  {
    free(output[i].data);
  }
  free(output);
  for (i = 0; i < npackets; i++)
  {
    free(input[i].data);
  }
  free(input);
  return 0;
}
