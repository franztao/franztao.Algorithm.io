from mininet.topo  import Topo
from mininet.net import Mininet
from mininet.node import CPULimitedHost
from mininet.link import TCLink
from mininet.util import dumpNodeConnections
from mininet.log import setLogLevel

class SingleSwicthTopo(Topo):
     "Single switch connnected to n hosts."
     def __init__(self,n=2,**opts):
         Topo.__init__(self,**opts)
         switch=self.addSwitch('s1')
         for h in range(n):
             #Each host gets 50%/n of system CPU
             host=self.addHost('h%s'%(h+1),cpu=.5/n)
             #10 Mbps,5ms delay,0% Loss,1000 packet Queue
             self.addLink(host,siwtch,bw=10,delay='5ms',loss=0,max_queue_size=1000,use_htb=True)