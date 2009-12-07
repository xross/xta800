TARGET = XC-5
BASE_DIR = ..
BINARY = iPodDock.xe

DEMO_SOURCE_DIRS = \
	audio/audioClass1v0 \
	audio/audioManager \
	iAP \
	portManager \
	iAP/extendedLingo \
	iAP/generalLingo \
	iAP/simpleLingo \
	iAP/displayLingo \
	iAP/audioLingo \
	iAP/equalizerLingo \
	iAP/storageLingo \
	iAP/ctrlLingo \
	iAP/transmit \
	flash \
	iPodHID \
	iPod \
	iic \
	coProcessor \
	support \
	IR \
	usb/epManager \
	.
	
	
LIB_DIRS += lib

SOURCE_DIRS += $(DEMO_SOURCE_DIRS)

INCLUDE_DIRS += $(DEMO_SOURCE_DIRS) $(LIB_DIRS)

EXCLUDE_FILES = 

XCC_FLAGS += -lflash $(TIMING) -O3 -g --show-report -Wall -save-temps

include $(BASE_DIR)/build/common/Makefile.common

all:  $(BIN_DIR)/$(BINARY)
	$(XOBJDUMP) --strip $(BIN_DIR)/$(BINARY)
	$(XOBJDUMP) -d $(BIN_DIR)/$(BINARY) > $(BIN_DIR)/$(BINARY).txt
	cd $(BIN_DIR) && $(XOBJDUMP) --split $(BINARY)

clean: clean_common
