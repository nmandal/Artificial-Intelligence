import java.util.*;

/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 */

public class NNImpl {
    private ArrayList<Node> inputNodes; //list of the output layer nodes.
    private ArrayList<Node> hiddenNodes;    //list of the hidden layer nodes
    private ArrayList<Node> outputNodes;    // list of the output layer nodes

    private ArrayList<Instance> trainingSet;    //the training set

    private double learningRate;    // variable to store the learning rate
    private int maxEpoch;   // variable to store the maximum number of epochs
    private Random random;  // random number generator to shuffle the training set

    /**
     * This constructor creates the nodes necessary for the neural network
     * Also connects the nodes of different layers
     * After calling the constructor the last node of both inputNodes and
     * hiddenNodes will be bias nodes.
     */

    NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Random random, Double[][] hiddenWeights, Double[][] outputWeights) {
        this.trainingSet = trainingSet;
        this.learningRate = learningRate;
        this.maxEpoch = maxEpoch;
        this.random = random;

        //input layer nodes
        inputNodes = new ArrayList<>();
        int inputNodeCount = trainingSet.get(0).attributes.size();
        int outputNodeCount = trainingSet.get(0).classValues.size();
        for (int i = 0; i < inputNodeCount; i++) {
            Node node = new Node(0);
            inputNodes.add(node);
        }

        //bias node from input layer to hidden
        Node biasToHidden = new Node(1);
        inputNodes.add(biasToHidden);

        //hidden layer nodes
        hiddenNodes = new ArrayList<>();
        for (int i = 0; i < hiddenNodeCount; i++) {
            Node node = new Node(2);
            //Connecting hidden layer nodes with input layer nodes
            for (int j = 0; j < inputNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
                node.parents.add(nwp);
            }
            hiddenNodes.add(node);
        }

        //bias node from hidden layer to output
        Node biasToOutput = new Node(3);
        hiddenNodes.add(biasToOutput);

        //Output node layer
        outputNodes = new ArrayList<>();
        for (int i = 0; i < outputNodeCount; i++) {
            Node node = new Node(4);
            //Connecting output layer nodes with hidden layer nodes
            for (int j = 0; j < hiddenNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
                node.parents.add(nwp);
            }
            outputNodes.add(node);
        }
    }

    /**
     * Get the prediction from the neural network for a single instance
     * Return the idx with highest output values. For example if the outputs
     * of the outputNodes are [0.1, 0.5, 0.2], it should return 1.
     * The parameter is a single instance
     */

    public int predict(Instance instance) {
        // TODO: add code here
        ArrayList<Double> x = instance.attributes;
        ArrayList<Integer> y = instance.classValues;

        forwardPass(x);

        double best_score = -Double.MAX_VALUE;
        int label = 0;
        for (int i = 0; i < y.size(); i++) {
            if (outputNodes.get(i).getOutput() > best_score) {
                best_score = outputNodes.get(i).getOutput();
                label = i;
            }
        }

        return label;
    }


    /**
     * Train the neural networks with the given parameters
     * <p>
     * The parameters are stored as attributes of this class
     */

    public void train() {
        // TODO: add code here
        for (int t = 0; t < maxEpoch; t++) {
            Collections.shuffle(trainingSet, random);

            trainingSet.forEach(instance -> {
                List<Double> x = instance.attributes;
                List<Integer> y = instance.classValues;

                forwardPass(x);
                backwardPass(y);
            });

            double loss = trainingSet.stream().mapToDouble(this::loss).average().orElse(Double.NaN);
            System.out.printf("Epoch: %s, Loss: %.8e\n", t, loss);
        }
    }

    private void forwardPass(List<Double> x) {
        for (int i = 0; i < x.size(); i++)
            inputNodes.get(i).setInput(x.get(i));

        hiddenNodes.forEach(Node::calculateOutput);
        outputNodes.forEach(Node::calculateOutput);
        double sum = outputNodes.stream().mapToDouble(Node::getOutput).sum();
        outputNodes.forEach(node -> node.normalizeOutput(sum));
    }

    private void backwardPass(List<Integer> y) {
        for (int i = 0; i < y.size(); i++)
            outputNodes.get(i).addGradient(y.get(i) - outputNodes.get(i).getOutput());

        outputNodes.forEach(Node::calculateDelta);
        outputNodes.forEach(Node::backProp);
        hiddenNodes.forEach(Node::calculateDelta);

        outputNodes.forEach(node -> node.updateWeight(learningRate));
        hiddenNodes.forEach(node -> node.updateWeight(learningRate));
    }

    private double loss(Instance instance) {
        ArrayList<Double> x = instance.attributes;
        ArrayList<Integer> y = instance.classValues;

        forwardPass(x);

        double p = outputNodes.get(y.indexOf(1)).getOutput();
        return -Math.log(p);
    }
}