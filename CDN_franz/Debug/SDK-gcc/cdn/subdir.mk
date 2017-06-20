################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../SDK-gcc/cdn/TabuSearch.cpp \
../SDK-gcc/cdn/another.cpp \
../SDK-gcc/cdn/cdn.cpp \
../SDK-gcc/cdn/deploy.cpp \
../SDK-gcc/cdn/graph.cpp \
../SDK-gcc/cdn/io.cpp \
../SDK-gcc/cdn/mcmf.cpp \
../SDK-gcc/cdn/minheap.cpp \
../SDK-gcc/cdn/solution.cpp \
../SDK-gcc/cdn/sort.cpp 

OBJS += \
./SDK-gcc/cdn/TabuSearch.o \
./SDK-gcc/cdn/another.o \
./SDK-gcc/cdn/cdn.o \
./SDK-gcc/cdn/deploy.o \
./SDK-gcc/cdn/graph.o \
./SDK-gcc/cdn/io.o \
./SDK-gcc/cdn/mcmf.o \
./SDK-gcc/cdn/minheap.o \
./SDK-gcc/cdn/solution.o \
./SDK-gcc/cdn/sort.o 

CPP_DEPS += \
./SDK-gcc/cdn/TabuSearch.d \
./SDK-gcc/cdn/another.d \
./SDK-gcc/cdn/cdn.d \
./SDK-gcc/cdn/deploy.d \
./SDK-gcc/cdn/graph.d \
./SDK-gcc/cdn/io.d \
./SDK-gcc/cdn/mcmf.d \
./SDK-gcc/cdn/minheap.d \
./SDK-gcc/cdn/solution.d \
./SDK-gcc/cdn/sort.d 


# Each subdirectory must supply rules for building sources it contributes
SDK-gcc/cdn/%.o: ../SDK-gcc/cdn/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I/home/franz/franzDocuments/eclipse4cworkspace/CDN_franz/SDK-gcc/cdn/lib -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


