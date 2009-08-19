
TEST_NAME 			= xtreme-v2

XCC							= xcc

SWC_USB 				= .
SWC_USB_TESTS  	= $(SWC_USB)/apps/audio

#Shared audio dir
AUD_SHARED			= $(SWC_USB_TESTS)/shared

#XUD
XUD_DIR					= $(SWC_USB)/xud
XUD_LIB_DIR    	= $(XUD_DIR)/lib
XUD_SRC_DIR			= $(XUD_DIR)/src
XUD_INC_DIR    	= $(XUD_DIR)/include
XUD_LIBS				= $(XUD_LIB_DIR)/xud.a
XUD_SRC_UIFM_DIR	= $(XUD_SRC_DIR)/uifm


TEST_DIR				= $(SWC_USB_TESTS)/$(TEST_NAME)
TEST_SRC_DIR		= $(TEST_DIR)/src

TEST_INCLUDES  	= -I $(XUD_INC_DIR) \
								-I $(XUD_SRC_DIR) \
			  				-I $(XUD_SRC_UIFM_DIR) \
			  				-I $(TEST_SRC_DIR) \
								-I $(AUD_SHARED)	

#ADAT            = -DADAT
ADAT            = -DSPDIF -DASYNC
BUILD_FLAGS   		= $(ADAT) -Wall -D UIFM_MODE=2 -fsubword-select \
									-D PRODUCT='"Xtreme "' \
					  -D VENDOR_LSB=0x92 -D VENDOR_MSB=0x0A -D PRODUCT_LSB=0x11 -D PRODUCT_MSB=0x70 -g -target=XS1-G4B-FB144
# -Wunused-parameter

TEST_SRC_S 			= 

TEST_SRC_s			= 
TEST_SRC_XC			= audio_xtreme physical_xtreme 

SHARED_SRC_XC		= spdif_audio_if midiuart midi2usbmidi codec hid sof_clock endpoint0 \
	                  XUD_DoEnumReqs audio codec_akm mixerCall deliverCall adat_tx spdif_tx_single
SHARED_SRC_S		= param deliverNew spdif_rx adat48000 adat44100 usbRecord

TARGET_DIR			= $(TEST_DIR)/bin
TARGET    			= $(TARGET_DIR)/$(TEST_NAME).xe

.SUFFIXES: .m4 .S

.m4.S:
	m4 $(ADAT) < $*.m4 > $*.S

$(TEST_NAME).xe: all

all: makedirs compile

buildxud:
	-@cd $(XMOS_ROOT)/swc_usb/xud
	-@make -f ./build/Makefile
	-@cd -

compile: $(AUD_SHARED)/deliverNew.S
	$(XCC) $(BUILD_FLAGS) $(TEST_INCLUDES) -o $(TARGET) \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .xc ,$(TEST_SRC_XC))) \
		$(addprefix $(AUD_SHARED)/, $(addsuffix .xc ,$(SHARED_SRC_XC))) \
		$(addprefix $(AUD_SHARED)/, $(addsuffix .S ,$(SHARED_SRC_S))) \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .S ,$(TEST_SRC_S))) $(XUD_LIBS)  \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .s ,$(TEST_SRC_s)))

xedump:
	xobjdump -D $(TARGET) > $(TARGET).txt
genxb:
	xobjdump --strip $(TARGET)
	-@echo ""
	-@ls -all $(TARGET_DIR)/$(TEST_NAME).xb


makedirs:
	-@mkdir -p $(TARGET_DIR)

clean:
	rm -rf $(TARGET_DIR) *.s
