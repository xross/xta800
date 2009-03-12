////////////////////////////////////////////////////////////////////////////
//
// void hda_io(chanend c_sync, chanend c_sdo, const unsigned morton1[256], const unsigned morton2[256], buffered in port:8 p_sync0, buffered in port:8 p_sync1, buffered in port:8 p_sdo0, buffered in port:8 p_sdo1, buffered out port:32 p_sdi);
//
// NOTE This is the "input bytes, output bytes" version
//
//  r0   c_sync
//  r1   c_sdo
//  r2   morton1
//  r3   morton2
//  r4   p_sync0
//  r5   p_sync1
//  r6   p_sdo0
//  r7   p_sdo1
//  r8   p_sdi
//  r9   scratch
// r10   scratch
// r11   scratch
//
.text
hda_io:
  entsp    16
init1:
  ldw      r4, sp[17]
  ldw      r5, sp[18]
  ldw      r6, sp[19]
  ldw      r7, sp[20]
  ldw      r8, sp[21]
  setsr    XS1_SR_FAST_MASK
sdr_byte0:
  in       r11, res[r4]
  ldw      r11, r2[r11]
  in       r10, res[r5]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
  in       r11, res[r6]
  ldw      r11, r2[r11]
  in       r10, res[r7]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
next_sdi_value:
  in       r9, res[r0]
sdr_byte1:
  in       r11, res[r4]
  ldw      r11, r2[r11]
  in       r10, res[r5]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
  in       r11, res[r6]
  ldw      r11, r2[r11]
  in       r10, res[r7]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
out_sdi:
  out      res[r8], r9
sdr_byte2:
  in       r11, res[r4]
  ldw      r11, r2[r11]
  in       r10, res[r5]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
  in       r11, res[r6]
  ldw      r11, r2[r11]
  in       r10, res[r7]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
sdr_byte3:
  in       r11, res[r4]
  ldw      r11, r2[r11]
  in       r10, res[r5]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
  in       r11, res[r6]
  ldw      r11, r2[r11]
  in       r10, res[r7]
  ldw      r10, r3[r10]
  or       r11, r11, r10
  outt     res[r0], r11
  shr      r11, r11, 8
  outt     res[r0], r11
	bu       sdr_byte0
