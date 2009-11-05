XUD_DIR = ./uifm/

XUD_SRC = \
  $(XUD_DIR)/XUD_UIFM_PConfig.S \
  $(XUD_DIR)/uifm_enable.S \
  $(XUD_DIR)/uifm_reg_read.S \
  $(XUD_DIR)/uifm_reg_write.S \
  $(XUD_DIR)/delay.S
  
EPMAN_DIR = ./EP_manager
  
EPMAN_SRC = \
  $(EPMAN_DIR)/bufferManager.c \
  $(EPMAN_DIR)/usb_lld_if.c \
  $(EPMAN_DIR)/EP_manager.c
    
HOST_SRC_S = \
  channels.S \
  inpw.S \
  lock.S \
  outpw.S \
  sr.S
  
HOST_SRC_XC = \
  host.xc \
  main.xc \
  lld.xc 
  
SRC = $(XUD_SRC) $(EPMAN_SRC) $(HOST_SRC_S) $(HOST_SRC_XC)

INCLUDE_DIR = \
  $(XUD_DIR) \
  $(EPMAN_DIR) \
  .
  
DEFINES = UIFM_MODE=2

TARGET = XS1-L1A-LQ64

XCC_FLAGS = -O2  





# ------------------------------------------------------------------------------------

all:
	xcc -g $(addprefix -I , $(INCLUDE_DIR)) $(addprefix -D , $(DEFINES)) -target=$(TARGET) $(SRC) 
