/*
 * Generic Audio Class 1v0 Defines
 */

#ifndef AUDIO_DEFINES_H_
#define AUDIO_DEFINES_H_

#define AUDIO_SET_CUR 0x01
#define AUDIO_SET_MIN 0x02
#define AUDIO_SET_MAX 0x03
#define AUDIO_SET_RES 0x04
#define AUDIO_SET_MEM 0x05

#define AUDIO_GET_CUR 0x81
#define AUDIO_GET_MIN 0x82
#define AUDIO_GET_MAX 0x83
#define AUDIO_GET_RES 0x84
#define AUDIO_GET_MEM 0x85

#define CS_ENDPOINT_UNDER 0x00
#define CS_ENDPOINT_SAMPLING_FREQ 0x01
#define CS_ENDPOINT_PITCH_CONTROL 0x02

#endif /*AUDIO_DEFINES_H_*/
