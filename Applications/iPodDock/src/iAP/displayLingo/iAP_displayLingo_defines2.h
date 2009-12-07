#ifndef IAP_DISPLAYLINGO_DEFINES2_H_
#define IAP_DISPLAYLINGO_DEFINES2_H_


typedef struct
{
  unsigned char formatID[2];
  unsigned char pixelFormat;
  unsigned char imageWidth[2];
  unsigned char imageHeight[2];
} s_artwork_format;

typedef struct
{
  unsigned char descriptorTelegramIndex[2];
  unsigned char displayPixelFormatCode;
  unsigned char imageWidth[2];
  unsigned char imageHeight[2];
  unsigned char insetRectTL[2][2];
  unsigned char insetRectBR[2][2];
  unsigned char rowsize[4];
} s_artwork_data;

#endif /*IAP_DISPLAYLINGO_DEFINES2_H_*/
