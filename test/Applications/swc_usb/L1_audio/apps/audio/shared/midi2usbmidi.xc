/**
 * @file midi2usbmidi.xc
 * @brief Generates USB MIDI events from MIDI events
 * @author Russel Gallop, XMOS Semiconductor 
 * @version 0.1
 */

#include <xs1.h>
#include <xclib.h>
#include <print.h>

#include "xud.h"


static int midiInputBuffer[128];   // TODO should only be 64
//static int midiInputIndex = 0;
static int bufferRequested = 0;
//unsigned needBuffer = 0;

int gotnote = 0;
unsigned midiInputIndex = 0;
/** 
 * @brief Sends USB MIDI event 
 *
 * @param[in]   ep    Endpoint number
 * @param[in]   c_ep  Channel to XUD
 * @param[in]   event MIDI event word
 */
int sendUSBMIDIEvent(unsigned ep, chanend c_ep, unsigned event) 
{
    event = byterev(event);
 
    if(midiInputIndex > 15 )
    {
        /* Drop for now */
    }
    else
    {
        midiInputBuffer [midiInputIndex] = event;
        midiInputIndex += 1;
        //needBuffer = 1;
    }

   
    /* Adding these two lines essentially (and removing sendcmd below) bypasses the buffering, in mac driver this is okay, 
    but if another driver polls the endpoint slower (i.e. < 1 per event time) things will break) */ 
    //midiInputBuffer[0] = event;
    //XUD_SetBuffer_int(c_ep, ep, midiInputBuffer, 4);
    
    return 1;
}


/** 
 * @brief Construct USB MIDI event 
 *
 */
 unsigned oldevent;
unsigned makeEvent(unsigned cable_number, unsigned codeIndexNumber, unsigned midi0, unsigned midi1, unsigned midi2) 
{
    unsigned event = 0;
    event |= (cable_number << 28);
    event |= (codeIndexNumber << 24);
    event |= (midi0 << 16);
    event |= (midi1 << 8);
    event |= (midi2 << 0);

    return event;
}


/**
 * @ brief MIDI input thread 
 *
 */
void midi_in(unsigned cable_number, unsigned ep, chanend c_ep, chanend c_midi_in) 
{
    // State for the parser
    unsigned inchan_1byte_msg = 0;
    unsigned chan_stat1_byte = 0;

    unsigned inchan_2byte_msg = 0;
    unsigned chan_2bytebuffer[2] = {0, 0};
    unsigned chan_2bytereceived = 0;
    unsigned chan_stat2_byte = 0;

    unsigned insysex_msg = 0;
    unsigned sysexbuffer[3] = {0, 0, 0};
    unsigned sysexreceived = 0;

    unsigned codeIndexNumber = 0;
    //unsigned midiInputIndex = 0;
    //unsigned bufferRequested = 0;
    int loop;
    unsigned tmp2;
    int i;
    unsigned datalength;
       
    while (1) 
    {
        loop = 1;
        
        midiInputIndex = 0;
        bufferRequested = 0;

        while(loop) //TODO Reset
        {
            // Read in each time
            unsigned b;
            unsigned highNibble;
            unsigned lowNibble;

            if(midiInputIndex > 0 && !bufferRequested)
            {
                XUD_RequestSendBuffer(c_ep, ep);
                bufferRequested = 1;
            }

            /* Wait for MIDI communcation from EP manager */
#pragma ordered
            select
            {
            
                /* Got MIDI byte... */ 
                case c_midi_in :> b:
                   
                    
                    highNibble = (b & 0xF0) >> 4;
                    lowNibble = (b & 0xF);

                    switch (highNibble) 
                    {
                        case 0x8: // Note-off
                        case 0x9: // Note-on
                        case 0xA: // Poly-KeyPress
                        case 0xB: // Control Change
                        case 0xE: // PitchBend Change
                        {
                            codeIndexNumber = highNibble;
                            chan_stat2_byte = b;
                            inchan_1byte_msg = 0;
                            inchan_2byte_msg = 1;
                            chan_2bytereceived = 0;
                            insysex_msg = 0;
                            sysexreceived = 0;
                            break;
                        }
        
                        case 0xC: // Program Change
                        case 0xD: // Channel Pressure
                        {
                            codeIndexNumber = highNibble;
                            chan_stat1_byte = b;
                            inchan_1byte_msg = 1;
                            inchan_2byte_msg = 0;
                            insysex_msg = 0;
                            chan_2bytereceived = 0;
                            sysexreceived = 0;
                            break;
                        }

                        case 0xF:
                        {
                            switch (lowNibble) 
                            {
                                case 0x8: // Timing tick
                                case 0xA: // Start song
                                case 0xB: // Continue song
                                case 0xC: // Stop song
                                case 0xE: // Active sensing
                                case 0xF: // System reset
                                    inchan_1byte_msg = 0;
                                    inchan_2byte_msg = 0;
                                    sendUSBMIDIEvent(ep, c_ep, makeEvent(cable_number, highNibble, b, 0, 0));
                                    break;
                                case 0x0: // Sysex start byte
                                    insysex_msg = 1;
                                    sysexbuffer[sysexreceived] = b;
                                    sysexreceived++;
                                    // Never send based on just this
                                    break;
                                case 0x7: // Sysex end byte
                                    insysex_msg = 0;
                                    sysexbuffer[sysexreceived] = b;
                                    sysexreceived++;
                                    // Compose sysex bytes that we've got and send them out.
                                    // This will depend how many we have.
                                    switch (sysexreceived) {
                                    case 1:
                                        sendUSBMIDIEvent(ep, c_ep, makeEvent(cable_number, 0x5,
                                            sysexbuffer[0], 0, 0));
                                        break;
                                    case 2:
                                        sendUSBMIDIEvent(ep, c_ep, makeEvent(cable_number, 0x6,
                                            sysexbuffer[0],  sysexbuffer[1], 0));
                                        break;
                                    case 3:
                                        sendUSBMIDIEvent(ep, c_ep, makeEvent(cable_number, 0x7,
                                            sysexbuffer[0],  sysexbuffer[1],  sysexbuffer[2]));
                                        break;
                                    default:
                                        break;
                                }
                                sysexreceived = 0;
                                break;
                            }
                            break;
                        }

                        case 0x0: // data MSD
                        case 0x1: // data MSD
                        case 0x2: // data MSD
                        case 0x3: // data MSD
                        case 0x4: // data MSD
                        case 0x5: // data MSD
                        case 0x6: // data MSD
                        case 0x7: // data MSD
                        {
                            if (inchan_2byte_msg) 
                            {
                               chan_2bytebuffer[chan_2bytereceived] = b;
                               chan_2bytereceived++;
                                if (chan_2bytereceived == 2) {
                                    sendUSBMIDIEvent(ep, c_ep, makeEvent(cable_number, codeIndexNumber,
                                        chan_stat2_byte, chan_2bytebuffer[0], chan_2bytebuffer[1]));
                                    chan_2bytereceived = 0;
                                }
                            }
                            if (inchan_1byte_msg) 
                            {
                                unsigned byte1 = b;
                                sendUSBMIDIEvent(ep, c_ep, makeEvent(cable_number, codeIndexNumber,
                                chan_stat1_byte, byte1, 0));
                            }
                            if (insysex_msg) {
                                sysexbuffer[sysexreceived] = b;
                                sysexreceived++;
                                // Output if have 3 using the SysEx starts or continues
                                if (sysexreceived == 3) {
                                    sendUSBMIDIEvent(ep, c_ep, makeEvent(cable_number, 0x4,
                                        sysexbuffer[0],  sysexbuffer[1],  sysexbuffer[2]));
                                    sysexreceived = 0;
                                }
                            }
                            break;
                        }
        
                    default:
                        // Data (or something we don't handle) -- ignore until
                        // we get a command to resync.
                        printstrln("didn't understand midi\n");
                        inchan_1byte_msg = 0;
                        inchan_2byte_msg = 0;
                        break;
                }

                break;

                 case inuint_byref(c_ep, tmp2):

                    /* Check for reset */
                    if(tmp2 == -1)
                    {
                        chkct(c_ep, XS1_CT_END);
                        outct(c_ep, XUD_CT_ACK);
                        outct(c_ep, XS1_CT_END);
                        loop = 0;
                    }
                    else
                    {

                        if(!bufferRequested)
                            printstr("buffernot requested!");
    
                        inct(c_ep);
           
                        datalength = (midiInputIndex) * 4;
                         
                        if(datalength == 0)
                             printstrln("datalength cannot be 0!"); 
        
                        /* Send datalength */
                        outuint(c_ep, datalength);   
                        
                        /* Send buffer */
#pragma unsafe arrays
                        for (i = 0; i < datalength >> 2; i++)
                        {
                            outuint(c_ep, midiInputBuffer[i]); 
                        }
                                       
                        /* Deal with tail */
                        if(datalength & 0x3)
                        {
                            outuint(c_ep, midiInputBuffer[i]);
                            printstr("Midi data had tail!\n");
                        }                              
                        /* Close down route and sync:*/
                        outct(c_ep, XS1_CT_END);


                        bufferRequested = 0;
                        //needBuffer = 0;
                        midiInputIndex = 0;

                    }
                    break;


            }
            
            /* Need this as currently can't break in select */
            if(loop == 0)
                break; 
            
        }
    }
}

// MIDI OUT
{unsigned, unsigned, unsigned, unsigned, unsigned} breakEvent(unsigned ev) {
    unsigned cable_number = (ev >> 28) & 0xf;
    unsigned codeIndexNumber = (ev >> 24) & 0xf;
    unsigned midi0 = (ev >> 16) & 0xff;
    unsigned midi1 = (ev >> 8) & 0xff;
    unsigned midi2 = (ev >> 0) & 0xff;
    return {cable_number, codeIndexNumber, midi0, midi1, midi2};
}

void sendMIDIFromEvent(chanend midi, unsigned event)
{ 
    unsigned cable_number; // ignore this for now!
    unsigned codeIndexNumber;
    unsigned midi0;
    unsigned midi1;
    unsigned midi2;

    {cable_number, codeIndexNumber, midi0, midi1, midi2} = breakEvent(event);
    // Not doing anything with cable number

    switch (codeIndexNumber) {
    case 0x4: // SysEx starts or continues
    case 0x7: // SysEx ends with the following three bytes
    case 0x8: // Note-off
    case 0x9: // Note-on
    case 0xA: // Poly-KeyPress
    case 0xB: // Control Change
    case 0xE: // PitchBend Change
    {
        midi <: midi0;
        midi <: midi1;
        midi <: midi2;
        break;
    }
    case 0x6: // SysEx ends with the following two bytes
    case 0xC: // Program Change
    case 0xD: // Channel Pressure
    {
        midi <: midi0;
        midi <: midi1;
        break;
    }
    case 0x5: // Single-byte System Common Message or SysEx ends with following single byte.
    case 0xF: // Single byte
    {
        midi <: midi0;
        break;
    }
    default:
        break;
    }
}


void midi_out(unsigned ep, chanend c_ep, chanend c_midi_out) {

    /* Note this should only have to be 64 but seems to be issue with OSX 10.5.7 which
    means wMaxPacketSize is exceeded */
    unsigned char buffer[1024]; 

    int loop;

    while(1)
    { 
        loop = 1;

        while(1)
        {
            int i;
            unsigned e;
            int datasent;
            datasent = XUD_GetBuffer_(c_ep, ep, buffer);

            /* Check for reset */
            if(datasent == -1)
            {
                break;
            }

            if ((datasent & 0x3) != 0) 
            {
                printstrln("Error: USB-MIDI not expecting data not in multiples of 4 bytes");
            }

            for (i = 0; i != datasent; i += 4) 
            {
                e = ((buffer[i] << 24) |
                    (buffer[i+1] << 16)) |
                    (buffer[i+2] << 8) |
                    (buffer[i+3]);
                sendMIDIFromEvent(c_midi_out, e);
            }
        }
    }
    
    /* Don't hit */
    return;
}
