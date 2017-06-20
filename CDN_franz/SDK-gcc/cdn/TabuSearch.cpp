#include "cdn.h"

void TabuSearchClass::Initset(int n, int c, int m) {
	tabuTableLength = m;
	Conlength = n;
	neighbourNum = c;
}


void TabuSearchClass::initGene() {
	memset(initNode, 0, sizeof(initNode));
	Sort4Consumer();

}
void TabuSearchClass::SetTabuTableLength() {
	tabuTableLength++;
}

int TabuSearchClass::Evaluat(int *p) {
	int i, n = 0;
	for (int i = 0; i < G.Nnum; i++)
		lastBestServer[i] = INF;
	for (i = 0; i < Conlength; i++)
		if (p[i] == 1)
			lastBestServer[n++] = i;
	G.chosNum = n;
	int eva = CostFlow_ZKW();
	return eva;
}

void TabuSearchClass::getNeighbour(int *p, int *q) {
	int n = 0;
	double ran2 = 0.0;
	double ran1 = 0.0;
	copyGene(p, q);
	for (int i = 0; i < G.Nnum; i++) {
		if (p[i] == 1) {
			n++;
		}
	}
	if (n >= G.Cnum) {
		do {
			ran1 = (rand() % n) / (double) n * n;
		} while (p[(int) ran1] != 1);
		q[(int) ran1] = 0;
	} else {

		ran2 = (rand() % Conlength) / (double) Conlength * Conlength;
		if (p[(int) ran2] == 0)
			q[(int) ran2] = 1;
		else
			q[(int) ran2] = 0;

	}
}

void TabuSearchClass::copyGene(int *p, int *q) {
	for (int i = 0; i < Conlength; i++)
		q[i] = p[i];
}

void TabuSearchClass::TabuTableFlush(int *p) {
	int i, j, k;
	for (i = 0; i < tabuTableLength - 1; i++)
		for (j = 0; j < Conlength; j++)
			tabuTable[i][j] = tabuTable[i + 1][j];
	for (k = 0; k < Conlength; k++)
		tabuTable[tabuTableLength - 1][k] = tempNode[k];
}
int TabuSearchClass::isTabuTable(int *p) {
	int i, j, flag = 0;
	for (i = 0; i < tabuTableLength; i++) {
		flag = 0;
		for (j = 0; j < Conlength; j++) {
			if (p[j] != tabuTable[i][j]) {
				flag = 1;
				break;
			}
		}
		if (flag == 0)
			break;
	}
	if (i == tabuTableLength)
		return 0;
	else
		return 1;
}


void TabuSearchClass::BeginSolve() {
	int n;
	bestTabuSearchValue = 0;
	number4Gene = 0;
	initGene();
	copyGene(initNode, bestNode);
	curtEvaluationValue = Evaluat(initNode);
	bestEvauValue = curtEvaluationValue;
	bestNodeNumber = G.chosNum;
	while (number4Gene < MAX_GENERATION) {
		n = 0;
		while (n < neighbourNum) {
			getNeighbour(initNode, tempNode);
			tempEvaluationValue = Evaluat(tempNode);
			if (isTabuTable(tempNode) != 0)
					{
				if (tempEvaluationValue < bestEvauValue) {
					copyGene(tempNode, currenttNode);
					curtEvaluationValue = tempEvaluationValue;
				}

				n++;
			} else if (isTabuTable(tempNode) == 0) {
				if (G.Nnum < 500) {
					if (n == 0) {
						if (tempEvaluationValue
								<= (int) curtEvaluationValue * 1.0005) {
							copyGene(tempNode, currenttNode);
							curtEvaluationValue = tempEvaluationValue;
							n++;
						}
						continue;
					}
				}
				if (tempEvaluationValue < curtEvaluationValue) {
					copyGene(tempNode, currenttNode);
					curtEvaluationValue = tempEvaluationValue;

				}
				n++;
			}
		}
		if (curtEvaluationValue < bestEvauValue) {
			bestTabuSearchValue = number4Gene;
			copyGene(currenttNode, bestNode);
			bestEvauValue = curtEvaluationValue;
			bestNodeNumber = G.chosNum;
		}

		if ((int) clock() / (CLOCKS_PER_SEC /*/ 1000*/) >= 87)
			break;
		number4Gene++;
		copyGene(currenttNode, initNode);
		TabuTableFlush(currenttNode);
	}
}
