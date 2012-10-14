package BTree;

import java.io.Serializable;
import java.util.ArrayList;

public class BNode implements Comparable, Serializable{

    public BNode()
    {
        leaf=false;
        //data=0;
        numvalues=0;
        parent=null;
        right=null;
        left=null;
    }
     public BNode parent,right,left;
     public int numvalues;
     public boolean leaf;
     public ArrayList<Integer> data=new ArrayList<Integer>();
     public ArrayList<Comparable> keyvalues=new ArrayList<Comparable>();
     //Comparable [] keyvalues= new Comparable[9];
     //BNode [] children=new BNode[10];
     public ArrayList<BNode> children=new ArrayList<BNode>();
    public int compareTo(Object o)
    {
         BNode n=(BNode)o;
         return keyvalues.get(0).compareTo(n.keyvalues.get(0));
         //return (keyvalues.get(0).key).compareTo(n.keyvalues.get(0).key);
     }
 

}
