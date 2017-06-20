################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../SDK-gcc/build/CMakeFiles/2.8.12.2/CompilerIdC/CMakeCCompilerId.c 

OBJS += \
./SDK-gcc/build/CMakeFiles/2.8.12.2/CompilerIdC/CMakeCCompilerId.o 

C_DEPS += \
./SDK-gcc/build/CMakeFiles/2.8.12.2/CompilerIdC/CMakeCCompilerId.d 


# Each subdirectory must supply rules for building sources it contributes
SDK-gcc/build/CMakeFiles/2.8.12.2/CompilerIdC/%.o: ../SDK-gcc/build/CMakeFiles/2.8.12.2/CompilerIdC/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


