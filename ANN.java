public class ANN {
    private float[][] iToHL;
    private float[][][] hlToHL;
    private float[][] hlToO;
    private int[] il;
    private float[][] hl;
    private float[] ol;
    static int HIDDEN_LAYERS;
    static int NODES_PER_LAYER;
    static int INPUTS;
    static int OUTPUTS;
    int genNum;
    ANN(int gen) {
        //name = new float[inputLayer][outputLayer][iterator/NA]
        iToHL = new float[INPUTS][NODES_PER_LAYER];
        hlToHL = new float[NODES_PER_LAYER+1][NODES_PER_LAYER][HIDDEN_LAYERS-1];
        hlToO = new float[NODES_PER_LAYER+1][OUTPUTS];
        //randomize
        for (int i = 0; i < iToHL.length; i++) { //NODES_PER_LAYER
            for (int j = 0; j < iToHL[0].length; j++) { //INPUTS
                iToHL[i][j] = -2+4*(float)Math.random();}}
        for (int i = 0; i < hlToHL.length; i++) {
            for (int j = 0; j < hlToHL[0].length; j++) {
                for (int k = 0; k < hlToHL[0][0].length;k++) {
                    hlToHL[i][j][k] = -2+4*(float)Math.random();}}}
        for (int i = 0; i < hlToO.length; i++) {
            for (int j = 0; j < hlToO[0].length; j++) {
                hlToO[i][j] = -2+4*(float)Math.random();}}
        hl = new float[HIDDEN_LAYERS][NODES_PER_LAYER];
        ol = new float[OUTPUTS];
        il = new int[INPUTS];
        genNum = gen;
    }
    ANN(int gen, ANN ann) {
        //deep copy
        iToHL = new float[INPUTS][NODES_PER_LAYER];
        hlToHL = new float[NODES_PER_LAYER+1][NODES_PER_LAYER][HIDDEN_LAYERS-1];
        hlToO = new float[NODES_PER_LAYER+1][OUTPUTS];
        hl = new float[HIDDEN_LAYERS][NODES_PER_LAYER];
        ol = new float[OUTPUTS];
        il = new int[INPUTS];
        genNum = gen;
        //copy the data forward
            for (int i = 0; i < iToHL.length; i++) {
                for (int j = 0; j < iToHL[0].length; j++) {
                    iToHL[i][j] = ann.getITOHL()[i][j];}}
            for (int i = 0; i < hlToHL.length; i++) {
                for (int j = 0; j < hlToHL[0].length; j++) {
                    for (int k = 0; k < hlToHL[0][0].length;k++) {
                        hlToHL[i][j][k] = ann.getHLTOHL()[i][j][k];}}}
            for (int i = 0; i < hlToO.length; i++) {
                for (int j = 0; j < hlToO[0].length; j++) {
                    hlToO[i][j] = ann.getHLTOO()[i][j];}}
            genNum = gen;}

    public static void setDat(int inputs, int layersDeep, int hiddensPerLayer) {
        INPUTS = inputs;
        HIDDEN_LAYERS = layersDeep;
        NODES_PER_LAYER = hiddensPerLayer;
        OUTPUTS = 3;} //mod3
    public void propagate() {
        //reset all nodes
        hl = new float[HIDDEN_LAYERS][NODES_PER_LAYER];
        ol = new float[OUTPUTS];
        ///mod5
//        for (int i = 0; i < hl.length; i++) {
//            for (int j = 0; j < hl[0].length; j++) {
//                hl[i][j] = 0;}}
//        for (int i = 0; i < ol.length; i++) {
//            ol[i] = 0;}
        //send data forward
        for (int i = 0; i < NODES_PER_LAYER; i++) { //output
            for (int j = 0; j < INPUTS; j++) { //input
                hl[0][i] += il[j]*iToHL[j][i];}}
        for (int i = 0; i < HIDDEN_LAYERS-1; i++) { //iterator
            for (int j = 0; j < NODES_PER_LAYER; j++) { //output
                for (int k = 0; k< NODES_PER_LAYER;k++) { //input
                    hl[i+1][j] += hl[0][k]*hlToHL[k][j][i];}
                hl[i+1][j]+=hlToHL[NODES_PER_LAYER][j][i];}}
        for (int i = 0; i < OUTPUTS; i++) { //output
            for (int j = 0; j< NODES_PER_LAYER;j++) { //input
                ol[i]+=hl[HIDDEN_LAYERS-1][j]*hlToO[j][i];}
            ol[i]+=hlToO[NODES_PER_LAYER][i];}
        //results in redefining the ol array
    }
    public void modify() {
        //only randomizes one set of weights at a time
        int randomSet = (int)(Math.random()*3);
        float anneal = (float)(0.4+Math.pow(2,-1*genNum));
        switch (randomSet) {
            case 0:
                for (int i = 0; i < iToHL.length; i++) { //NODES_PER_LAYER
                    for (int j = 0; j < iToHL[0].length; j++) { //INPUTS
                        iToHL[i][j] += -anneal+(2*anneal*(float)Math.random());}}
                break;
            case 1:
                for (int i = 0; i < hlToHL.length; i++) {
                    for (int j = 0; j < hlToHL[0].length; j++) {
                        for (int k = 0; k < hlToHL[0][0].length;k++) {
                            hlToHL[i][j][k] += -anneal+(2*anneal*(float)Math.random());}}}
                break;
            case 2:
                for (int i = 0; i < hlToO.length; i++) {
                    for (int j = 0; j < hlToO[0].length; j++) {
                        hlToO[i][j] += -anneal+(2*anneal*(float)Math.random());}}
                break;
        }}
        public void genBump() {genNum++;}
        public float[][] getITOHL() {return iToHL;}
        public float[][][] getHLTOHL() {return hlToHL;}
        public float[][] getHLTOO() {return hlToO;}
        public float[][] getHL() {return hl;}
        public float[] getOL() {return ol;}
        public int[] getIL() {return il;}
        //mod6
        public void buildIL(int [] sensor, int[] apple) {il = new int [] {sensor[0],sensor[1],sensor[2], apple[0],apple[1]};}
    }
