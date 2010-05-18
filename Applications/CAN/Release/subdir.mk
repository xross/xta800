################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../CanCFunctions.c 

XN_SRCS += \
../XC-1.xn 

XC_SRCS += \
../CanFunctions.xc \
../CanPhy.xc \
../CanTest.xc 

OBJS += \
./CanCFunctions.o \
./CanFunctions.o \
./CanPhy.o \
./CanTest.o 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.c
	@echo 'Building file: $<'
	@echo 'Invoking: C Compiler'
	xcc -O2 -g -Wall -c -std=c99 -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '

%.o: ../%.xc
	@echo 'Building file: $<'
	@echo 'Invoking: XC Compiler'
	xcc -O3 -g -Wall -c -o "$@" "$<" "../XC-1.xn"
	@echo 'Finished building: $<'
	@echo ' '


