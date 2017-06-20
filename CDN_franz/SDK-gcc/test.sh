#!/bin/bash
./build.sh
cp -r /home/zhang/workspace/huawei/case_example/h3.txt ./bin/
cd bin
./cdn h3.txt result.txt
cd ..
