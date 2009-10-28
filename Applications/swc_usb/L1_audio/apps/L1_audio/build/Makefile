
TEST_NAME 			= L1_audio

XCC							= xcc

SWC_USB 				= .
SWC_USB_TESTS  	= $(SWC_USB)/apps/

#Shared audio dir
AUD_SHARED			= $(SWC_USB_TESTS)/audio/shared

#XUD
XUD_DIR					= $(SWC_USB)/xud_lite
XUD_LIB_DIR    	= $(XUD_DIR)/lib
XUD_SRC_DIR			= $(XUD_DIR)/src
XUD_INC_DIR    	= $(XUD_DIR)/include
XUD_SRC_UIFM_DIR	= $(XUD_SRC_DIR)/uifm


XUD_SRC_XC		= XUD_Manager \
		  			XUD_DeviceAttach \
						XUD_LLD_Io \
	     			XUD_EpFunctions  
 
XUD_SRC_S 	= XUD_LLD_IoLoop \
						ledTrapHandler \
						ssctrl XUD_EpFuncs

XUD_SRC_C	=

XUD_SRC_UIFM_S  = XUD_UIFM_PConfig \
		  uifm_enable \
		  uifm_reg_write \
		  setAddress \
		  uifm_reg_read
XUD_SRC_SUP_S	= delay \
		  outpw \
		  sr \
		  inpw \
		  lock channels 



TEST_DIR				= $(SWC_USB_TESTS)/$(TEST_NAME)
TEST_SRC_DIR		= $(TEST_DIR)/src

TEST_INCLUDES  	= -I $(XUD_INC_DIR) \
								-I $(XUD_SRC_DIR) \
			  				-I $(XUD_SRC_UIFM_DIR) \
			  				-I $(TEST_SRC_DIR) \
								-I $(AUD_SHARED)	

#ADAT            = -DADAT
ADAT            = -DSPDIF
BUILD_FLAGS   		= -DASYNC $(ADAT) -g -Wall -O3 -save-temps -D UIFM_MODE=2 -fsubword-select \
									-D PRODUCT='"Xtreme "' \
					  -D VENDOR_LSB=0x92 -D VENDOR_MSB=0x0A -D PRODUCT_LSB=0x11 -D PRODUCT_MSB=0x70 -target=XS1-L1A-TQ128 -D G1

TEST_SRC_S 			= 

TEST_SRC_s			= 
TEST_SRC_XC			= main endpoint0 XUD_DoEnumReqs mixerCall

SHARED_SRC_XC		=  
SHARED_SRC_S		= 

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

compile: 			
						$(XCC) $(BUILD_FLAGS) $(TEST_INCLUDES) -o $(TARGET) \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .xc ,$(TEST_SRC_XC))) \
		$(addprefix $(AUD_SHARED)/, $(addsuffix .xc ,$(SHARED_SRC_XC))) \
		$(addprefix $(AUD_SHARED)/, $(addsuffix .S ,$(SHARED_SRC_S))) \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .S ,$(TEST_SRC_S))) \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .s ,$(TEST_SRC_s))) \
		$(addsuffix .o ,$(XUD_SRC_XC)) \
		$(addsuffix .o ,$(XUD_SRC_S)) \
		$(addsuffix .o ,$(XUD_SRC_SUP_S)) \
		$(addsuffix .o ,$(XUD_SRC_UIFM_S)) 

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
