
# The copyrights, all other intellectual and industrial
# property rights are retained by XMOS and/or its licensors.
# Terms and conditions covering the use of this code can
# be found in the Xmos End User License Agreement.
#
# Copyright XMOS Ltd 2008
#
# Author: Suleiman Abu Kharmeh
# Date: 19/November/2008


SLASH = /

AES_SOURCE_DIR	=   .$(SLASH)source

TARGETNAME	= aes_test

#this is for the XCC compiler
AESFILESBASIC	=	$(AES_SOURCE_DIR)$(SLASH)AES.xc \
				$(AES_SOURCE_DIR)$(SLASH)DecryptTables.xc \
				$(AES_SOURCE_DIR)$(SLASH)EncryptTables.xc \
				$(AES_SOURCE_DIR)$(SLASH)InvKeyExpansionTables.xc \
				$(AES_SOURCE_DIR)$(SLASH)sBoxInv.xc \
				$(AES_SOURCE_DIR)$(SLASH)sBox.xc \

AESFILESASSEMBLY = $(AES_SOURCE_DIR)$(SLASH)Decrypt.S \
				$(AES_SOURCE_DIR)$(SLASH)Encrypt.S \
				$(AES_SOURCE_DIR)$(SLASH)InvKeyExpansion.S \
				$(AES_SOURCE_DIR)$(SLASH)KeyExpansion.S \

INCLUDES 	= -I $(AES_SOURCE_DIR) \

all: assembly

assembly:
	xcc $(AESFILESBASIC) $(AESFILESASSEMBLY) -o $(TARGETNAME)_assembly.xe \
				
