/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

unsigned countOnes(unsigned input)
{
  int i;
  unsigned retVal=0;
  for (i=0; i<16; i++)
  {
    if (input & 1)
      retVal++;
    input>>=1;
  }
  return retVal;
}

