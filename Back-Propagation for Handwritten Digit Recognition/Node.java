import java.util.*;

/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details.
 * Feel free to modify the provided functions to fit your own implementation
 */

public class Node {
    private int type = 0; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
    public ArrayList<NodeWeightPair> parents = null; //Array List that will contain the parents (including the bias node) with weights if applicable

    private double inputValue = 0.0;
    private double outputValue = 0.0;
    private double outputGradient = 0.0;
    private double delta = 0.0; //input gradient

    //Create a node with a specific type
    Node(int type) {
        if (type > 4 || type < 0) {
            System.out.println("Incorrect value for node type");
            System.exit(1);

        } else {
            this.type = type;
        }

        if (type == 2 || type == 4) {
            parents = new ArrayList<>();
        }
    }

    //For an input node sets the input value which will be the value of a particular attribute
    public void setInput(double inputValue) {
        if (type == 0) {    //If input node
            this.inputValue = inputValue;
        }
    }

    /**
     * Calculate the output of a node.
     * You can get this value by using getOutput()
     */
    public void calculateOutput() {
        if (type == 2 || type == 4) {   //Not an input or bias node
            // TODO: add code here
            inputValue = 0;
            for (NodeWeightPair p : parents)
                inputValue += p.node.getOutput() * p.weight;
            if (type == 2)  outputValue = Math.max(0, inputValue);
            if (type == 4)  outputValue = Math.exp(inputValue);
        }
    }

    public void normalizeOutput(double sum) {
        if (type == 4)
            outputValue /= sum;
    }

    //Gets the output value
    public double getOutput() {

        if (type == 0) {    //Input node
            return inputValue;
        } else if (type == 1 || type == 3) {    //Bias node
            return 1.00;
        } else {
            return outputValue;
        }

    }

    public void addGradient(double gradient) {
        if (type == 2 || type == 4) {
            outputGradient += gradient;
        }
    }

    //Calculate the delta value of a node.
    public void calculateDelta() {
        if (type == 2 || type == 4)  {
            // TODO: add code here
            if (type == 2)  delta = (inputValue > 0) ? outputGradient : 0;
            if (type == 4)  delta = outputGradient;
            outputGradient = 0;
        }
    }

    public void backProp() {
        if (type == 4)
            parents.forEach(p -> p.node.addGradient(p.weight * delta));
    }

    //Update the weights between parents node and current node
    public void updateWeight(double learningRate) {
        if (type == 2 || type == 4) {
            // TODO: add code here
            parents.forEach(p -> p.weight += learningRate * p.node.getOutput() * delta);
        }
    }
}