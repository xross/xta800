
/* Defines for USB audio */


/* A.4 Audio Interface Class Code */
#define AUDIO 0x1

/* A.5 Audio Interface Subclass Codes */
#define AUDIOCONTROL    0x01
#define AUDIOSTREAMING   0x02

/* A.6 Audo Interface Protocol Codes */
#define IP_VERSION_02_00 0x20

/* A.7 Audio Function Category Codes */
#define DESKTOP_SPEAKER 0x01
#define HOME_THEATER 0x02
#define IO_BOX 0x08
#define PRO_AUDIO 0x0A

/* A.9 Audio Class-Specific AC Interface Descriptor Subtypes */
#define HEADER          0x1
#define OUTPUT_TERMINAL 0x03
#define MIXER_UNIT      0x04
#define SELECTOR_UNIT   0x05
#define CLOCK_SOURCE    0x0A
#define CLOCK_SELECTOR  0x0B

/* A.10 Audio Class Specific AS Interface Descriptor Subtypes */
#define AS_GENERAL  0x01

/* A.13 Audio Class-Specific Endpoint Descriptor Subtypes */
#define EP_GENERAL 0x01

/* Audio Class-Specific Descriptor Types */
#define CS_INTERFACE 0x24
#define CS_ENDPOINT 0x25


/* Audio Class-Specific AS Interface Descriptor Subtypes */
#define FORMAT_TYPE 0x02



/* From Audio Formats */
////////////////////////


/* From USB Device Class Definition for Terminal Types */
////////////////////////////////////////////////////////

/* 2.1 USB Terminal Types */
#define USB_STREAMING 0x0101

/* A.1 Format Type Codes */
#define FORMAT_TYPE_I   0x01

/* A.2 AudioData Format Bit Allocatoion in the bmFormats field */
/* A.2.1 Audio Data Format Type I Bit Allocations */
#define PCM 0x01
#define PCM8 0x02
#define IEEE_FLOAT 0x04
