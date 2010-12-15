// Copyright 2009 Fredrik Petrini
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
// and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
// THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

#ifndef SID_DEMO_H_
#define SID_DEMO_H_

#define SAMPLE_RATE 44100
#define BUFFER_SIZE 882

void sid_player(chanend c_audio, chanend c_led, chanend c_tunes1, chanend c_tunes2, chanend c_tunes3);
void buffer(chanend c_producer, chanend c_consumer);
void ffter(short fromBuf[], unsigned int outBuf[]);
void sid_store_core_1(chanend c);
void sid_store_core_2(chanend c);
void sid_store_core_3(chanend c);

extern void cpuJSR(unsigned short npc, unsigned char na);
extern void c64Init(int nSampleRate);
extern void synth_render(signed short &buffer, unsigned long len);

extern unsigned char mm_get(unsigned short addr);
extern void mm_set(unsigned short addr, unsigned char value);


#endif /*SID_DEMO_H_*/
