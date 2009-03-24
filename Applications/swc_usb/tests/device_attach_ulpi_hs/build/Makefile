
TEST_NAME 		= device_attach_ulpi_hs

XCC			= xcc

SWC_USB			= $(XMOS_ROOT)/swc_usb
SWC_USB_TESTS  = $(SWC_USB)/tests

#XUD
XUD_DIR			= $(SWC_USB)/xud
XUD_LIB_DIR    		= $(XUD_DIR)/lib
XUD_SRC_DIR		= $(XUD_DIR)/src
XUD_INC_DIR    		= $(XUD_DIR)/include
XUD_LIBS		= $(XUD_LIB_DIR)/xud.a
XUD_SRC_UIFM_DIR= $(XUD_SRC_DIR)/uifm

TEST_DIR		= $(SWC_USB_TESTS)/$(TEST_NAME)
TEST_SRC_DIR		= $(TEST_DIR)/src

TEST_INCLUDES  		= -I $(XUD_INC_DIR) \
			  -I $(XUD_SRC_DIR) \
			  -I $(XUD_SRC_UIFM_DIR) \
			  -I $(TEST_SRC_DIR) 

BUILD_FLAGS   		= -Wall -O3 -save-temps -D UIFM_MODE=2 -fall-stream-select  

TEST_SRC_S 		= param 

TEST_SRC_XC		= device_attach ep1_thread audio_init_audioBoard audio_host 

TARGET_DIR		= $(TEST_DIR)/bin
TARGET    		= $(TARGET_DIR)/$(TEST_NAME).xe

$(TEST_NAME).xe: all

all: makedirs compile xedump genxb

buildxud:
	-@cd $(XMOS_ROOT)/swc_usb/xud
	-@make -f ./build/Makefile
	-@cd -

#TODO Remove XUD source files.  This is due to bug in liker not finding symbols in lib
compile:
	$(XCC) $(BUILD_FLAGS) $(TEST_INCLUDES) -o $(TARGET) \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .xc ,$(TEST_SRC_XC))) \
		$(addprefix $(TEST_SRC_DIR)/, $(addsuffix .S ,$(TEST_SRC_S))) $(XUD_LIBS)  

	   

xedump:
	xobjdump -D $(TARGET) > $(TARGET).txt
genxb:
	xobjdump --strip $(TARGET)
	-@echo ""
	-@ls -all $(TARGET_DIR)/$(TEST_NAME).xb


makedirs:
	-@mkdir -p $(TARGET_DIR)

clean:
	rm -rf $(TARGET_DIR)
