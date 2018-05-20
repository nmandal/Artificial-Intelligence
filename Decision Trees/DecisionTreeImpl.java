import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.lang.Math;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  //map for getting the index
  private HashMap<String,Integer> label_inv;
  private HashMap<String,Integer> attr_inv;
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {

    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    // TODO: add code here
    root = buildTree(train.instances,majority_label(train.instances), "", attributes);
  }
  DecTreeNode buildTree(List<Instance> ins, String default_label, String parentAttributeValue, List<String> remaining){
      if(ins.size() == 0)
          return new DecTreeNode(default_label,"", parentAttributeValue, true);
      else if(same_label(ins))
          return new DecTreeNode(ins.get(0).label,"", parentAttributeValue, true);
      else if(remaining.size() == 0)
          return new DecTreeNode(default_label,"", parentAttributeValue, true);

      String attribute = "";
      double ig = -1;
      int best = 0;
      for(int i=0;i<remaining.size();i++){
          double cur = InfoGain(ins, getAttributeIndex(remaining.get(i)));
          if(cur > ig)
          {
              ig = cur;
              best = i;
          }
      }
      DecTreeNode temp = new DecTreeNode(majority_label(ins), remaining.get(best), parentAttributeValue, false);
      List<String> values = attributeValues.get(remaining.get(best));
      List<String> childRemains = new ArrayList<String>(remaining);
      childRemains.remove(remaining.get(best));
      int global_attr = getAttributeIndex(remaining.get(best));
      for(int i=0;i<values.size();i++)
      {
          List<Instance> childins = new ArrayList<Instance>();
          for(int j=0;j< ins.size();j++)
          {
              if(ins.get(j).attributes.get(global_attr).equals(values.get(i)))
              {
                  childins.add(ins.get(j));
              }
          }
          if(childins.size() == 0)
            temp.addChild(buildTree(childins, default_label,  values.get(i),childRemains));
          else
            temp.addChild(buildTree(childins, majority_label(childins), values.get(i), childRemains));
      }


      return temp;
  }

  boolean same_label(List<Instance> instances){
      for(int i = 0;i < instances.size();i++){
          if(!instances.get(i).label.equals(instances.get(0).label))
              return false;
      }
      return true;
  }
  String majority_label(List<Instance> instances){
    if(instances.size() == 0)
        return "";
    int max_count = 0;
    int maj_lab = 0;

    for(int i=0;i<labels.size();i++){
        int count = 0;
        for(int j=0;j<instances.size();j++){
            if(instances.get(j).label.equals(this.labels.get(i)))
                count++;
        }
        if(count > max_count){
            maj_lab = i;
            max_count = count;
        }
    }
    return this.labels.get(maj_lab);
  }


  double Entropy(List<Instance> ins){
      if(ins.size() == 0)
          return 0.0;
      int vec[] = new int[this.labels.size()];
      for(int i=0;i<ins.size();i++){
          vec[getLabelIndex(ins.get(i).label)]++;
      }
      double ent = 0;
      for(int i=0;i<labels.size();i++){
          if(vec[i] == 0) continue;
          double pi = (double)vec[i] / (double)ins.size();
          ent -= pi * Math.log(pi) / Math.log(2);
      }
      return ent;
  }
  double InfoGain(List<Instance> ins, int attr){
      double ent;
      ent = Entropy(ins);
      double cond_ent;
      cond_ent = 0;
      List<String> values = attributeValues.get(attributes.get(attr));
      for(int i=0;i<values.size();i++){
          List<Instance> temp = new ArrayList<Instance>();
          for(int j=0;j<ins.size();j++){
              if(ins.get(j).attributes.get(attr).equals(values.get(i)))
                  temp.add(ins.get(j));
          }
          cond_ent += Entropy(temp) * (double)(temp.size()) / (double)(ins.size());
      }
      
      return ent-cond_ent;
  }
  @Override
  public String classify(Instance instance) {
      for(int i=0; i < labels.size();i++)
        {
            label_inv.put(labels.get(i),i);
        }
        for(int i=0; i < attributes.size();i++)
        {
            attr_inv.put(attributes.get(i),i);
        }
    return classify_recursive(instance, root);
  }
  private String classify_recursive(Instance instance, DecTreeNode node)
  {
      if(node.terminal)
          return node.label;
      int attr = attr_inv.get(node.attribute);
      for(int i=0;i<node.children.size();i++)
      {
          if(node.children.get(i).parentAttributeValue.equals(instance.attributes.get(attr)))
              return classify_recursive(instance, node.children.get(i));
      }
      return "No attri";
  }
  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    if(label_inv == null){
        this.label_inv = new HashMap<String,Integer>();
        for(int i=0; i < labels.size();i++)
        {
            label_inv.put(labels.get(i),i);
        }
    }
    if(attr_inv == null){
        this.attr_inv = new HashMap<String,Integer>();
        for(int i=0; i < attributes.size();i++)
        {
            attr_inv.put(attributes.get(i),i);
        }
    }
    // TODO: add code here
    List<Instance> instances = train.instances;
    
    for(int i=0;i< attributes.size();i++)
    {
        double cur = InfoGain(instances, i);
        System.out.println(attributes.get(i) + " " + String.format("%.5f", cur));
    }

  }
  @Override
  public void printAccuracy(DataSet test) {
    // TODO: add code here
    if(label_inv == null){
        this.label_inv = new HashMap<String,Integer>();
        for(int i=0; i < labels.size();i++)
        {
            label_inv.put(labels.get(i),i);
        }
    }
    if(attr_inv == null){
        this.attr_inv = new HashMap<String,Integer>();
        for(int i=0; i < attributes.size();i++)
        {
            System.out.println(attributes.get(i));
            attr_inv.put(attributes.get(i),i);
        }
    }
    // TODO: add code here
    int correct = 0;
    List<Instance> ins = test.instances;
    
    if(ins.size() == 0)
        System.out.println("0.00000");
    for(int i=0;i<ins.size();i++)
    {
        if(classify(ins.get(i)).equals(ins.get(i).label))
            correct++;
    }
    System.out.println(String.format("%.5f", (double)correct / (double) ins.size()));
    return;
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   */
  public void print() {

    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    if(label_inv == null){
        this.label_inv = new HashMap<String,Integer>();
        for(int i=0; i < labels.size();i++)
        {
            label_inv.put(labels.get(i),i);
        }
    }
    return label_inv.get(label);
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    if(attr_inv == null)
    {
        this.attr_inv = new HashMap<String,Integer>();
        for(int i=0; i < attributes.size();i++)
        {
            attr_inv.put(attributes.get(i),i);
        }
    }
    return attr_inv.get(attr);
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
}
