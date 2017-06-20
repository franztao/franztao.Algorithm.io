#!/usr/bin/python
 
from mininet.net import Mininet
from mininet.node import Node
from mininet.link import TCLink
from mininet.log import  setLogLevel, info
from threading import Timer
from mininet.util import quietRun
from time import sleep
 
def myNet(cname='controller', cargs='-v ptcp:'):
    "create network from scratch using Open vSwitch."
    info("*** Createing nodes\n")
    controller=Node('c0',inNamespace=False)
    switch=Node('s1',inNamespace=False)
    switch1=Node('s1',inNamespace=False)
    h0=Node('h0')
    h1=Node('h1')
    
    