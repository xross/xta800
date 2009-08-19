#ifndef _ssctrl_h_
#define _ssctrl_h_

// SSCTRL registers:
// SSWITCH_DEVICE_ID0_NUM            0
// SSWITCH_DEVICE_ID1_NUM            1
// SSWITCH_DEVICE_ID2_NUM            2
// SSWITCH_DEVICE_ID3_NUM            3
// SSWITCH_NODE_CONFIG_NUM           4
// SSWITCH_NODE_ID_NUM               5
// SSWITCH_PLL_CTL_NUM               6
// SSWITCH_DCDC_ACCESS_CTL_NUM       7
// SSWITCH_DCDC_TRIM_NUM             8
// SSWITCH_DCDC_OUTPUT_CTL_NUM       9
// SSWITCH_SLINK_0_NUM + 0..15       32..47
// SSWITCH_PLINK_0_NUM + 0..15       64..79
// SSWITCH_XLINK_0_NUM + 0..15       128..143

unsigned ssctrl_read(int regn);
void ssctrl_check(int regn, unsigned x);
void ssctrl_write(int regn, unsigned x);
void ssctrl_delay(int ntmrticks);

#endif
