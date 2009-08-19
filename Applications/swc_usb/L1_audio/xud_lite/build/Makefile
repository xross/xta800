
#Makefile for XUD (XMOS USB Driver) libs

XCC		= xcc

#SWC_USB        	= $(XMOS_ROOT)/swc_usb
SWC_USB				= .

XUD_DIR	       	= $(SWC_USB)/xud_lite

XUD_SRC_DIR	= $(XUD_DIR)/src
XUD_SRC_UIFM_DIR= $(XUD_SRC_DIR)/uifm
XUD_SRC_SUP_DIR = $(XUD_SRC_DIR)/support

XUD_LIB_DIR    	= $(XUD_DIR)/lib
XUD_INC_DIR    	= $(XUD_DIR)/include

XUD_INCLUDES	= -I $(XUD_INC_DIR) \
		  -I $(XUD_SRC_DIR) \
                  -I $(XUD_SRC_UIFM_DIR) \
		  -I $(XUD_SRC_SUP_DIR)

BUILD_FLAGS 	= -Wall -g -O3 -D UIFM_MODE=2  -fasm-linenum -fcomment-asm  -fall-stream-select -D G1 

XUD_LIB			= xud.a

#XUD src
XUD_SRC_XC		= XUD_Manager \
		  			XUD_DeviceAttach \
						XUD_LLD_Io \
	     			XUD_EpFunctions  
 
XUD_SRC_S 	= XUD_LLD_IoLoop \
						ledTrapHandler \
						ssctrl XUD_EpFuncs

XUD_SRC_C	=

#XUD/UIFM src
XUD_SRC_UIFM_S  = XUD_UIFM_PConfig \
		  uifm_enable \
		  uifm_reg_write \
		  setAddress \
		  uifm_reg_read
#XUD/support src
XUD_SRC_SUP_S	= delay \
		  outpw \
		  sr \
		  inpw \
		  lock channels 


all: cleanlibs makedirs buildlibs postclean 

makedirs:
	-@mkdir -p $(XUD_LIB_DIR)

cleanlibs:
	-@rm -rf $(XUD_LIB_DIR)/*.a

buildlibs:
	-@echo ""
	-@echo "     * Building XUD lib"

	$(XCC) $(BUILD_FLAGS) $(XUD_INCLUDES) -c \
		$(addprefix $(XUD_SRC_DIR)/, $(addsuffix .xc ,$(XUD_SRC_XC))) \
		$(addprefix $(XUD_SRC_DIR)/, $(addsuffix .S ,$(XUD_SRC_S))) \
		$(addprefix $(XUD_SRC_SUP_DIR)/, $(addsuffix .S ,$(XUD_SRC_SUP_S))) \
		$(addprefix $(XUD_SRC_UIFM_DIR)/, $(addsuffix .S ,$(XUD_SRC_UIFM_S))) 
	ar rvu $(XUD_LIB_DIR)/$(XUD_LIB) \
		$(addsuffix .o ,$(XUD_SRC_XC)) \
		$(addsuffix .o ,$(XUD_SRC_S)) \
		$(addsuffix .o ,$(XUD_SRC_SUP_S)) \
		$(addsuffix .o ,$(XUD_SRC_UIFM_S)) 
	ranlib $(XUD_LIB_DIR)/$(XUD_LIB) 

postclean:
	-@rm -rf *.o

cleantemps:
	-@rm -rf *.xi *.o *.s
