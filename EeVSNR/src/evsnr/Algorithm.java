/**
 * 
 */

package evsnr;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import standardalgorithm.ShortestPath;
import substratenetwork.BackupNode;
import substratenetwork.SubstrateNetwork;
import virtualnetwork.SurvivalVirtualNetwork;
import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * Algorithm.
 * 
 * @author franz
 *
 */
public class Algorithm {
  private Logger algorithmLog = Logger.getLogger(Algorithm.class);

  public String algorithmName;
  private SubstrateNetwork sn;
  private VirtualNetworkParameter vnp;

  // algorithm
  // NoShared Shared
  // FD FI
  // FD: ILP EVSNR
  // EVSNR: Min Ran
  private boolean isShared;
  private boolean isFailDep;
  private boolean isExact;
  // 0 Ran 1 Min
  private int sequence;

  public Algorithm() {
    PropertyConfigurator.configure("log4j.properties");
  }

  /**
   * releaseResource.
   * 
   * @param isForceRelease
   * 
   */
  public void releaseResource(boolean isForceRelease) {
    for (int i = 0; i < this.sn.virNetCollection.size(); i++) {
      if (this.sn.virNetCollection.get(i).getIsRunning()) {
        this.sn.virNetCollection.get(i)
            .setLeaveTime(this.sn.virNetCollection.get(i).getLeaveTime() - 1);
        if (isForceRelease || (0 == this.sn.virNetCollection.get(i).getLeaveTime())) {
          releaseVNResource(i);
          this.sn.virNetCollection.get(i).setIsRunning(false);
          if (this.sn.virNetCollection.get(i).surVirNet.isSucceedEmbed) {
            releaseEnVirNetResource(i);
          }
        }
      }
    }

  }

  /**
   * releaseEVNResource.
   */
  private void releaseEnVirNetResource(int index) {
    if (!this.isShared) {
      for (int i = 0; i < this.sn.surVirNetCollection.get(index).nodeSize; i++) {
        if (this.sn.surVirNetCollection.get(index).nodeComputation4Backup[i] > 0) {
          this.sn.nodeComputation4SurvivalUnsharedBackupSum[this.sn.surVirNetCollection
              .get(index).surNode2subNode[i]] -= this.sn.surVirNetCollection
                  .get(index).nodeComputation4Backup[i];
        }
      }
    }
    if (!this.isShared) {
      for (int i = 0; i < this.sn.surVirNetCollection.get(index).nodeSize; i++) {
        for (int j = 0; j < i; j++) {
          if (this.sn.surVirNetCollection.get(index).edgeBandwith4Backup[i][j] > 0) {
            for (int p = 0; p < this.sn.surVirNetCollection.get(index).eEdge2sPath.get(i).get(j)
                .size() - 1; p++) {
              int from = this.sn.surVirNetCollection.get(index).eEdge2sPath.get(i).get(j).get(p);
              int to = this.sn.surVirNetCollection.get(index).eEdge2sPath.get(i).get(j).get(p + 1);
              this.sn.edgeBandwith4SurvivalUnsharedBackupSum[from][to] -= this.sn.surVirNetCollection
                  .get(index).edgeBandwith4Backup[i][j];

              this.sn.edgeBandwith4SurvivalUnsharedBackupSum[to][from] = this.sn.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
            }
          }
        }
      }
    }

    this.sn.surVirNetCollection.get(index).destructerResource();

  }

  /**
   * @param index
   * 
   */
  private void releaseVNResource(int index) {

    // node
    for (int i = 0; i < this.sn.virNetCollection.get(index).nodeSize; i++) {
      this.sn.nodeComputation4Former[this.sn.virNetCollection.get(
          index).vNode2sNode[i]] -= this.sn.virNetCollection.get(index).nodeComputationDemand[i];
    }

    // edge
    for (int i = 0; i < this.sn.virNetCollection.get(index).nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if (this.sn.virNetCollection.get(index).edgeBandwithDemand[i][j] > 0) {
          for (int p = 0; p < this.sn.virNetCollection.get(index).vEdge2sPath.get(i).get(j).size()
              - 1; p++) {
            int from = this.sn.virNetCollection.get(index).vEdge2sPath.get(i).get(j).get(p);
            int to = this.sn.virNetCollection.get(index).vEdge2sPath.get(i).get(j).get(p + 1);
            this.sn.edgeBandwith4Former[from][to] -= this.sn.virNetCollection
                .get(index).edgeBandwithDemand[i][j];
            this.sn.edgeBandwith4Former[to][from] = this.sn.edgeBandwith4Former[from][to];
          }
        }
      }
    }

    this.sn.virNetCollection.get(index).destructerResource();
  }

  /**
   * constructVirtualNetwork.
   * 
   * @param vn
   *          vn
   * @param sameVn
   *          sameVn
   * @param vnp
   *          vnp
   * @return boolean
   */
  private boolean constructVirtualNetwork(VirtualNetwork vn, VirtualNetwork sameVn) {

    // LeaveTime
    if (Parameter.isSameVirNet4EveryTime) {
      vn.setLeaveTime(sameVn.getLeaveTime());
    } else {
      vn.setLeaveTime((int) (Parameter.VNRequestsContinueTimeMinimum + Math.random()
          * (Parameter.VNRequestsContinueTimeMaximum - Parameter.VNRequestsContinueTimeMinimum)));
    }

    // node
    boolean[] isSamesNode = new boolean[this.sn.nodeSize];
    for (int i = 0; i < vn.nodeSize; i++) {
      // the virtual node map whose physical node
      int snodeloc;
      if (Parameter.isSameVirNet4EveryTime) {
        snodeloc = sameVn.vNode2sNode[i];
        vn.vNode2sNode[i] = snodeloc;
        vn.nodeServiceType[i] = sameVn.nodeServiceType[i];
        vn.nodeComputationDemand[i] = sameVn.nodeComputationDemand[i];
      } else {
        do {
          snodeloc = (int) Math.round(Math.random() * (this.sn.nodeSize - 1));
          if (!isSamesNode[snodeloc]) {
            vn.vNode2sNode[i] = snodeloc;
            isSamesNode[snodeloc] = true;
            break;
          }
        } while (true);
        // service
        int nodeservice = this.sn.vectorServiceTypeSet.get(snodeloc).elementAt(
            (int) Math.random() * (this.sn.vectorServiceTypeSet.get(snodeloc).size() - 1));
        vn.nodeServiceType[i] = nodeservice;
        // node demand
        vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math.round(
            Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
      }
      if (vn.nodeComputationDemand[i] > this.sn.getSubstrateRemainComputaion4VirNet(snodeloc,
          this.isShared)) {
        algorithmLog
            .warn("Fail to embed virtual network (" + i + ")-th node into substrate network");
        return false;
      } else {
        vn.nodeComputationCapacity[i] = this.sn.nodeComputationCapacity[snodeloc]
            - this.sn.getSubstrateRemainComputaion4VirNet(snodeloc, this.isShared);
        this.sn.nodeComputation4Temp[snodeloc] += vn.nodeComputationDemand[i];
      }

    }

    // edge
    for (int i = 0; i < vn.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if ((Parameter.isSameVirNet4EveryTime && sameVn.topology[i][j])
            || ((Parameter.isSameVirNet4EveryTime == false)
                && (Math.random() < vnp.node2nodeProbability))) {
          int distributeIthEdgeBandwith = 0;
          // System.out.println("distributeIthEdgeBandwith
          // "+distributeIthEdgeBandwith);
          if (vn.vNode2sNode[i] != vn.vNode2sNode[j]) {
            // exist edge's path,bandwith
            // source destination bandwith[][]
            int[][] tempBandwith = new int[this.sn.nodeSize][this.sn.nodeSize];
            int[][] tempTopo = new int[this.sn.nodeSize][this.sn.nodeSize];
            for (int k = 0; k < this.sn.nodeSize; k++) {
              for (int l = 0; l < k; l++) {
                int rbw = this.sn.getSubStrateRemainBandwith4VN(k, l, this.isShared);
                if (rbw > 0) {
                  tempBandwith[k][l] = tempBandwith[l][k] = rbw;
                  tempTopo[l][k] = tempTopo[k][l] = 1;
                }
              }
            }

            ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
            List<Integer> pathList = new LinkedList<Integer>();
            pathList = shortestPath.dijkstra(vn.vNode2sNode[i], vn.vNode2sNode[j], tempTopo);

            if (pathList == null) {
              algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to "
                  + j + ")-edge into SN : feasible path is null");
              return false;
            }
            if (pathList.isEmpty()) {
              algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to "
                  + j + ")-edge into SN : feasible path is is empty");
              return false;
            }
            // set virtual network's edge bandwith
            int pathMaximumBandwith = 0;
            vn.vEdge2sPath.get(i).get(j).addElement(pathList.get(0));
            for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
              int e = pathList.get(k);
              pathMaximumBandwith = Math.max(pathMaximumBandwith, tempBandwith[s][e]);
              vn.vEdge2sPath.get(i).get(j).addElement(e);
              s = e;
            }
            for (int k = pathList.size() - 1; k >= 0; k--) {
              vn.vEdge2sPath.get(j).get(i).addElement(pathList.get(k));
            }
            if (Parameter.isSameVirNet4EveryTime) {
              distributeIthEdgeBandwith = sameVn.edgeBandwithDemand[i][j];
            } else {
              distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum + Math
                  .round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
            }
            if (distributeIthEdgeBandwith > pathMaximumBandwith) {
              algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to "
                  + j + ") edge into SN :SN edge resource not more than PathBandwith Demand");
              return false;
            }

            for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
              int e = pathList.get(k);
              this.sn.edgeBandwith4Temp[s][e] += distributeIthEdgeBandwith;
              this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
              s = e;
            }
          }
          vn.topology[i][j] = vn.topology[j][i] = true;
          vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeIthEdgeBandwith;
        }
      }
    }

    // modify node and edge new value
    for (int i = 0; i < sn.nodeSize; i++) {
      this.sn.nodeComputation4Former[i] += this.sn.nodeComputation4Temp[i];
      this.sn.nodeComputation4Temp[i] = 0;
      for (int j = 0; j < i; j++) {
        if (i != j) {
          this.sn.edgeBandwith4Former[i][j] += this.sn.edgeBandwith4Temp[i][j];
          this.sn.edgeBandwith4Former[j][i] = this.sn.edgeBandwith4Former[i][j];
          this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
        }
      }
    }
    for (int i = 0; i < vn.nodeSize; i++) {
      this.sn.virNetIndexSet4sNode.get(vn.vNode2sNode[i])
          .addElement(this.sn.virNetCollection.size());
      for (int j = 0; j < vn.nodeSize; j++) {
        for (int k = 0; k < vn.vEdge2sPath.get(i).get(j).size() - 1; k++) {
          this.sn.virNetIndexSet4Edge.get(vn.vEdge2sPath.get(i).get(j).get(k))
              .get(vn.vEdge2sPath.get(i).get(j).get(k + 1))
              .addElement(this.sn.virNetCollection.size());
        }
      }
    }
    return true;
  }

  /**
   * generateAndProtectVirNet.
   * 
   * @param sameVirNet
   *          sameVirNet
   */
  public void generateAndProtectVirNet(VirtualNetwork sameVirNet) {

    this.sn.virNetSum++;
    VirtualNetwork vn;
    if (null == sameVirNet) {
      vn = new VirtualNetwork(this.vnp);
    } else {
      vn = new VirtualNetwork(sameVirNet);
    }

    // construct a virtual network
    if (constructVirtualNetwork(vn, sameVirNet)) {
      vn.setIndex(this.sn.virNetCollection.size());
      vn.setIsRunning(true);
      this.sn.virNetSuceedEmbedSum++;
      this.sn.virNetCollection.addElement(vn);
      algorithmLog.info("Alg: " + this.algorithmName + " Succeed to generate ("
          + (this.sn.virNetCollection.size() - 1) + ")-th  VN");
    } else {
      this.sn.clearTempResource();
      algorithmLog.warn("Alg: " + this.algorithmName + " Fail to generate ("
          + this.sn.virNetCollection.size() + ")-th VN");
      return;
    }

    // construct backup node
    BackupNode bn = new BackupNode(this.sn, vn, this.isShared);

    // construct SurvivalVirtualNetwork
    SurvivalVirtualNetwork svn = new SurvivalVirtualNetwork(this.sn, vn, bn);
    vn.surVirNet = svn;

    if (generateSurvivalVirtualNetwork(svn)) {
      this.sn.surVirNetSuceedEmbedSum++;
      algorithmLog.info("Alg: " + this.algorithmName + " Succeed to construct EVN and embed EVN");
    } else {
      this.sn.clearTempResource();
      algorithmLog.warn("Alg: " + this.algorithmName + " Fail to construct EVN");
    }
    this.sn.surVirNetCollection.addElement(svn);
    return;
  }

  /**
   * generateSurvivalVirtualNetwork.
   * 
   * @param vn
   *          vn
   * @return boolean
   */
  private boolean generateSurvivalVirtualNetwork(SurvivalVirtualNetwork svn) {

    if (svn.startSurvivalVirtualNetwork(this.isExact, this.isFailDep, this.sequence)) {
      if (distributeResource4EVN(svn)) {
        svn.isSucceedEmbed = true;
        algorithmLog
            .info("Alg: " + this.algorithmName + " Succeed distribute enough resource 4 SVN");
        return true;
      } else {
        if (Parameter.IsReleaseVNafterEVNFailure) {
          this.sn.virNetCollection.get(this.sn.virNetCollection.size() - 1).setLeaveTime(1);
        }
        algorithmLog.info("Alg: " + this.algorithmName + " Fail distribute enough resource 4 SVN");
      }
    } else {
      algorithmLog
          .info("Alg: " + this.algorithmName + " Algorithm Fail to obtain the solution of SVN");
    }
    return false;
  }

  /**
   * @param evn
   * @return
   */
  private boolean distributeResource4EVN(SurvivalVirtualNetwork evn) {
    for (int i = 0; i < this.sn.nodeSize; i++) {
      int nodeResource = 0;
      for (int j = 0; j < evn.nodeSize; j++) {
        if (i == evn.surNode2subNode[j]) {
          if (j < evn.virNet.nodeSize) {
            if (evn.nodeComputationConsume[j] - evn.virNet.nodeComputationDemand[j] > 0) {
              nodeResource += evn.nodeComputationConsume[j] - evn.virNet.nodeComputationDemand[j];
            }
          } else {
            if (evn.nodeComputationConsume[j] > 0) {
              nodeResource += evn.nodeComputationConsume[j];
            }
          }
        }
      }
      if (nodeResource > 0) {
        if (nodeResource < this.sn.getSubstrateRemainComputaion4SurVirNet(i, this.isShared)) {
          this.sn.nodeComputation4Temp[i] += nodeResource;
        } else {
          algorithmLog
              .warn("Fail to distribute enhacned network (" + i + ") node into substrate network");
          return false;
        }
      }
      if (nodeResource < 0) {
        algorithmLog.error("distributeResource4EVN nodeResource less than zero ");
      }

    }

    for (int i = 0; i < evn.nodeSize; i++) {
      for (int j = 0; j < evn.nodeSize; j++) {
        if (i < evn.nodeSize4Failure && j < evn.nodeSize4Failure) {

          evn.edgeBandwith4Backup[i][j] = evn.edgeBandwith4Comsume[i][j]
              - evn.virNet.edgeBandwithDemand[i][j];
        } else {
          evn.edgeBandwith4Backup[i][j] = evn.edgeBandwith4Comsume[i][j];
        }
      }
    }
    // edge
    for (int i = 0; i < evn.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if (evn.edgeBandwith4Backup[i][j] > 0) {
          int tempTopology[][];
          tempTopology = new int[this.sn.nodeSize][this.sn.nodeSize];
          for (int k = 0; k < this.sn.nodeSize; k++) {
            for (int l = 0; l < this.sn.nodeSize; l++) {
              int bandwith = this.sn.getSubStrateRemainBandwith4EVN(k, l, this.isShared);
              if (bandwith > evn.edgeBandwith4Backup[i][j]) {
                tempTopology[k][l] = tempTopology[l][k] = 1;
              }
            }
          }

          ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
          List<Integer> pathList = new LinkedList<Integer>();

          // do not split shortest path
          pathList = shortestPath.dijkstra(evn.surNode2subNode[i], evn.surNode2subNode[j], tempTopology);
          if (pathList == null) {
            algorithmLog.warn("Fail to embedd enhanced network (" + i + "--" + j
                + ") edge into substrate network: null value");
            return false;
          }
          if (pathList.isEmpty()) {
            algorithmLog.warn("Fail to embedd enhanced network (" + i + "--" + j
                + ") edge into substrate network: lack path");
            return false;
          }

          // set virtual network's edge bandwith
          for (int k = 0; k < pathList.size(); k++) {
            evn.eEdge2sPath.get(i).get(j).addElement(pathList.get(k));
          }
          for (int k = pathList.size() - 1; k >= 0; k--) {
            evn.eEdge2sPath.get(j).get(i).addElement(pathList.get(k));
          }

          for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
            int e = pathList.get(k);
            this.sn.edgeBandwith4Temp[s][e] += evn.edgeBandwith4Backup[i][j];
            this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
            s = e;
          }
        } else {
          if (evn.edgeBandwith4Backup[i][j] < 0) {
            algorithmLog.error("distributeResource4EVN edgeResource less than zero ");
            return false;
          }
        }
      }
    }

    for (int i = 0; i < evn.nodeSize; i++) {
      this.sn.surVirNetIndexSet4sNode.get(evn.surNode2subNode[i])
          .addElement(this.sn.surVirNetCollection.size());
      for (int j = 0; j < evn.nodeSize; j++) {
        for (int k = 0; k < evn.eEdge2sPath.get(i).get(j).size() - 1; k++) {
          this.sn.surVirNetIndexSet4Edge.get(evn.eEdge2sPath.get(i).get(j).get(k))
              .get(evn.eEdge2sPath.get(i).get(j).get(k + 1))
              .addElement(this.sn.surVirNetCollection.size());
        }
      }
    }

    // set node and edge new value
    for (int i = 0; i < evn.nodeSize; i++) {
      if (i < evn.virNet.nodeSize) {
        evn.nodeComputation4Backup[i] = evn.nodeComputationConsume[i]
            - evn.virNet.nodeComputationDemand[i];
      } else {
        evn.nodeComputation4Backup[i] = evn.nodeComputationConsume[i];
      }
    }

    for (int i = 0; i < sn.nodeSize; i++) {
      if (!this.isShared) {
        this.sn.nodeComputation4SurvivalUnsharedBackupSum[i] += this.sn.nodeComputation4Temp[i];
      }
      this.sn.nodeComputation4Temp[i] = 0;
      for (int j = 0; j < i; j++) {
        if (!this.isShared) {
          this.sn.edgeBandwith4SurvivalUnsharedBackupSum[i][j] += this.sn.edgeBandwith4Temp[i][j];
          this.sn.edgeBandwith4SurvivalUnsharedBackupSum[j][i] = this.sn.edgeBandwith4SurvivalUnsharedBackupSum[i][j];
        }
        this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
      }

    }
    return true;

  }

  /**
   * @return the sn
   */
  public SubstrateNetwork getSn() {
    return sn;
  }

  /**
   * @param sn
   *          the sn to set
   */
  public void setSn(SubstrateNetwork sn) {
    this.sn = sn;
  }

  /**
   * @return the isShared
   */
  public boolean isShared() {
    return isShared;
  }

  /**
   * @param isShared
   *          the isShared to set
   */
  public void setShared(boolean isShared) {
    this.isShared = isShared;
  }

  /**
   * @return the isFD
   */
  public boolean isFD() {
    return isFailDep;
  }

  /**
   * @param isFD
   *          the isFD to set
   */
  public void setFD(boolean isFD) {
    this.isFailDep = isFD;
  }

  /**
   * @return the isExact
   */
  public boolean isExact() {
    return isExact;
  }

  /**
   * @param isExact
   *          the isExact to set
   */
  public void setExact(boolean isExact) {
    this.isExact = isExact;
  }

  /**
   * @return the sequence
   */
  public int getSequence() {
    return sequence;
  }

  /**
   * @param sequence
   *          the sequence to set
   */
  public void setSequence(int sequence) {
    this.sequence = sequence;
  }

  /**
   * @return the vnp
   */
  public VirtualNetworkParameter getVnp() {
    return vnp;
  }

  /**
   * @param vnp
   *          the vnp to set
   */
  public void setVnp(VirtualNetworkParameter vnp) {
    this.vnp = vnp;

  }

  /**
   * 
   */
  public void isClearAllResource() {

    for (int i = 0; i < this.sn.nodeSize; i++) {
      if (this.sn.nodeComputation4Former[i] > 0) {
        algorithmLog.error("substrate (" + i + ") node former resource fail release completely");
        break;
      }
    }

    for (int i = 0; i < this.sn.nodeSize; i++) {
      if (this.sn.nodeComputation4SurvivalUnsharedBackupSum[i] > 0) {
        algorithmLog.error("substrate (" + i + ") node enhance resource fail release completely");
        break;
      }
    }

    for (int i = 0; i < this.sn.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if (this.sn.edgeBandwith4Former[j][i] > 0) {
          algorithmLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
              + ") edge former resource fail release completely");
          break;
        }

      }
    }

    for (int i = 0; i < this.sn.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if (this.sn.edgeBandwith4SurvivalUnsharedBackupSum[j][i] > 0) {
          algorithmLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
              + ") edge enhance resource fail release completely");
          break;
        }

      }
    }

  }

  /**
   * setParameter.
   * 
   * @param algName
   *          algName
   * @param sn
   *          sn
   * @param isExact
   *          isExact
   * @param failureindependent
   *          failureindependent
   * @param isShared
   *          isShared
   */
  public void setParameter(String algName, SubstrateNetwork sn, boolean isExact,
      boolean failureindependent, boolean isShared) {
    this.algorithmName = algName;
    this.sn = sn;
    this.isExact = isExact;
    this.isFailDep = failureindependent;
    this.isShared = isShared;
  }

}
