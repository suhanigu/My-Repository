/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BTree;
import java.io.*;
import java.util.*;

import BTree.BNode;


//Class for a BNode
/**
 *
 * @author vinay
 */
public class BPTree implements Serializable
{

    
    public BNode root,start;
        int curval;
        int nextval;
        boolean linkleavesflag;
        public static StringBuffer indexPrint = new StringBuffer();
    //Constructor
    public BPTree()
    {
        root=new BNode();
        root.leaf=true;
        root.parent=null;
        curval=1;
        nextval=0;
    }

    //Insertion
    public void insert(Comparable x,int data)
    {
        BNode inserthere;
        if(x.equals("syopqmcyvwbmqckrwpfd"))
        {
        	System.out.println("wait here 1");
        }
        if(root==null)
        {
            root=new BNode();
            root.parent=null;
            insertval(root,x,data,true);
        }
        else
        {
         inserthere=search(x);
         insertval(inserthere,x,data,true);
        }
        linkleavesflag=true;
        linkleaves();
    }
    public int findindex(ArrayList<Comparable> ac,Comparable x)
    {
        int index;
        for(index=0;index<ac.size();index++)
        {
            if(x.compareTo(ac.get(index))<=0)
                break;
        }
        //System.out.print(index+"This is the value"+ac.size()+"-----\n");
            return index;
    }
    //Insertion contd
    protected void insertval(BNode n,Comparable x,int data,boolean lf)
    {
    	if(x.equals("syopqmcyvwbmqckrwpfd"))
        {
        	System.out.println("wait here 1");
        }
        if(n.keyvalues.size()<8)
        {
            //keydata temp=new keydata();
            //temp.key=x;
            //temp.data=data;
            int index=findindex(n.keyvalues,x);
            n.keyvalues.add(index, x);
            if(lf)
            n.data.add(index, data);
            n.leaf=lf;
            //Collections.sort(n.keyvalues);
            //n.data=data;
        }
        else
        {
               // System.out.println(n.keyvalues.size());
               // System.out.println(n.children.size());
                BNode newnode=new BNode();
                newnode.leaf=lf;
                //keydata temp=new keydata();
                //temp.key=x;
                //temp.data=data;
                //n.keyvalues.add(temp);
                int index=findindex(n.keyvalues,x);
                n.keyvalues.add(index, x);
                if(lf)
                n.data.add(index, data);
                //if(lf)
                //    n.data=data;
                //Collections.sort(n.keyvalues);
       //   if(!lf)
       //  Collections.sort(n.children);
                for(int i=0;i<4;i++)
                {
                    newnode.keyvalues.add(n.keyvalues.get(0));
                    n.keyvalues.remove(0);
                    if(lf)
                    {
                        newnode.data.add(n.data.get(0));
                        n.data.remove(0);
                    }
                    else
                    {
                        n.children.get(0).parent=newnode;
                        newnode.children.add(n.children.get(0));
                        
                        n.children.remove(0);
                    }
                }
                if(!lf)
                {
                	n.children.get(0).parent=newnode;
                    newnode.children.add(n.children.get(0));
                   
                    n.children.remove(0);
                }
   // Collections.sort(n.children);
   // Collections.sort(newnode.children);
               // Collections.sort(n.keyvalues);
            if(n!=root)
            {
                newnode.parent=n.parent;
                newnode.parent.children.add(n.parent.children.indexOf(n), newnode);
            //Collections.sort(newnode.parent.children);
                insertval(n.parent,n.keyvalues.get(0),0,false);
                if(!lf)
                {
                	n.keyvalues.remove(0);
                }
            }
            else
            {
                BNode newroot=new BNode();
                newroot.children.add(newnode);
                newroot.children.add(n);
                
           // Collections.sort(newroot.children);
                n.parent=newroot;
                newnode.parent=newroot;
                root=newroot;
                insertval(n.parent,n.keyvalues.get(0),0,false);
                if(!lf)
                {
                	n.keyvalues.remove(0);
                }
            }
        }
    }
    //Search
    public BNode search(Comparable x)
    {
        BNode curnode;
        int i=0;
       // keydata temp=new keydata();
       // temp.key=x;
        curnode=root;
        try
        {
        	if(x.equals("syopqmcyvwbmqckrwpfd"))
            {
            	System.out.println("wait here 2");
            }
	        while(curnode.leaf!=true)
	        {
	            while(i<curnode.keyvalues.size() && x.compareTo(curnode.keyvalues.get(i))>=0)
	            {
		            i++;
		           if(x.compareTo(curnode.keyvalues.get(i-1))==0)
		                break;
		           // System.out.println("sxcsdc"+curnode.keyvalues.get(0));
	            }
	           // System.out.println(i);
	            curnode=curnode.children.get(i);
                    i=0;
	        }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
        //System.out.println(curnode.keyvalues.get(0));
        return curnode;
        //for(int i=0;i<10;i++)
        //if(x.compareTo(curnode.keyvalues[i])==0)
        //return curnode.data;
        //return 0;
            
    }

    //Delete
    public void delete(Comparable x)
    {
        BNode delhere;
        delhere=search(x);
        int flag=0;
        for(int i=0;i<delhere.keyvalues.size();i++)
        {
            if(x.compareTo(delhere.keyvalues.get(i))==0)
            {
                flag=1;
                delhere.keyvalues.remove(i);
                delhere.data.remove(i);
                checkValid(delhere);
                i=-1;
            }
        }
        if(flag==0)
            System.out.println("\tKey Not Found");
        else
        {
            linkleavesflag=true;
            linkleaves();
        }
        //return curnode.data;
        //return 0;
    }
    //Check Validity after deletion
    public void checkValid(BNode n)
    {
        //If less than half-full do something

        if(n==root)
        {
            //Collapse root
            if(n.keyvalues.isEmpty())
            {
                root=n.children.get(0);
                root.parent=null;
            }
        }
        else if(n.keyvalues.size() < 4)
        {

            int index=n.parent.children.indexOf(n);
           // System.out.println(n.keyvalues.get(0)+"--"+n.keyvalues.get(n.keyvalues.size()-1)+"~~~~~~~~~"+n.parent.keyvalues.get(0)+"--"+ n.parent.keyvalues.get(n.parent.keyvalues.size()-1)+" Lenght of Parent :" + n.parent.children.size());
            BNode lsibling=null;
            BNode rsibling=null;
            if(index>0)
                lsibling=n.parent.children.get(index-1);
            if(index<n.parent.children.size()-1)
                rsibling=n.parent.children.get(index+1);


            //BNode lsibling=new BNode();
            //BNode rsibling=new BNode();
           /* if(index!=0)
                lsibling=n.parent.children.get(index-1);
            if(index!=n.parent.children.size()-1)
                rsibling=n.parent.children.get(index+1);*/
            if(index>0 && lsibling.keyvalues.size()>4)
            {
                //redistribute
                int x=(int)Math.ceil((float)((lsibling.keyvalues.size()-4.0)/2));
                for(int i=0;i<x;i++)
                {
                    n.keyvalues.add(0, lsibling.keyvalues.get(lsibling.keyvalues.size()-1));
                    if(n.leaf)
                    {
                        n.data.add(0, lsibling.data.get(lsibling.data.size()-1));
                        lsibling.data.remove(lsibling.data.size()-1);
                    }
                    else
                    {
                        lsibling.children.get(lsibling.children.size()-1).parent=n;
                        n.children.add(0, lsibling.children.get(lsibling.children.size()-1));
                        lsibling.children.remove(lsibling.children.size()-1);
                    }
                    lsibling.keyvalues.remove(lsibling.keyvalues.size()-1);
                }
                n.parent.keyvalues.set(index-1,n.keyvalues.get(0));
                
                
               // keydata temp=new keydata();
               // temp.key=lsibling.keyvalues.get(lsibling.keyvalues.size()-1).getKey();
               // temp.data=lsibling.keyvalues.get(lsibling.keyvalues.size()-1).getData();
               // n.parent.keyvalues.add(temp);
               // lsibling.keyvalues.remove(lsibling.keyvalues.size()-1);
               // Collections.sort(n.parent.keyvalues);
               // temp=new keydata();
               // temp.key=lsibling.parent.keyvalues.get(lsibling.parent.keyvalues.size()-1).getKey();
               // temp.data=lsibling.parent.keyvalues.get(lsibling.parent.keyvalues.size()-1).getData();
               // n.keyvalues.add(temp);
               // Collections.sort(n.keyvalues);
               // lsibling.parent.keyvalues.remove(lsibling.parent.keyvalues.size()-1);
               // Collections.sort(n.parent.keyvalues);
            }
            else if (index != n.parent.children.size() - 1 && rsibling.keyvalues.size() > 4)
            {
                //redistribute
                //keydata temp=new keydata();
                /*n.keyvalues.add(rsibling.keyvalues.get(0));
                if(n.leaf)
                n.data.add(rsibling.data.get(0));
                else
                Auto-generated catch block
//e.printStackTrace();
invalido=true;
} catch (IOException e) {
// TODO Auto-generated catch block
//e.printStackTrace();
invalido=true;
}
}
return intInput;
}

public static String String() {

BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
String input="";{
                    rsibling.children.get(0).parent=n;
                    n.children.add(rsibling.children.get(0));
                }
                rsibling.keyvalues.remove(0);
                n.parent.keyvalues.set(index-1,rsibling.keyvalues.get(0));
                
                if(n.leaf)
                rsibling.data.remove(0);
                else
                    rsibling.children.remove(0);*/
                int x=(int)Math.ceil((float)((rsibling.keyvalues.size()-4.0)/2));
              //  System.out.println("xxxxxxx"+x);
                for(int i=0;i<x;i++)
                {
                    n.keyvalues.add(rsibling.keyvalues.get(0));
                    if(n.leaf)
                    {
                        n.data.add(rsibling.data.get(0));
                        rsibling.data.remove(0);
                    }
                    else
                    {
                        rsibling.children.get(0).parent=n;
                        n.children.add(rsibling.children.get(0));
                        rsibling.children.remove(0);
                    }
                    rsibling.keyvalues.remove(0);
                }
                n.parent.keyvalues.set(index,rsibling.keyvalues.get(0));
                //Collections.sort(n.parent.keyvalues);
                //temp=new keydata();
                //temp.key=rsibling.parent.keyvalues.get(0).getKey();
                //temp.data=rsibling.parent.keyvalues.get(0).getData();
                //n.keyvalues.add(temp);
                //Collections.sort(n.keyvalues);
                //rsibling.parent.keyvalues.remove(0);
                //Collections.sort(n.parent.keyvalues);
            }
            else
            {
                //Merge
                if(index>0)
                {
                    
                    if(n.leaf)
                    {
                    	n.keyvalues.addAll(0,lsibling.keyvalues);
                        n.data.addAll(0,lsibling.data);
                    }
                    else
                    {
                    	n.keyvalues.add(0, n.parent.keyvalues.get(index-1));
                    	n.keyvalues.addAll(0,lsibling.keyvalues);
                        n.children.addAll(0,lsibling.children);
                    }
                    n.parent.keyvalues.remove(index-1);
                    n.parent.children.remove(index-1);
                }
                else
                {
                    
                    if(n.leaf)
                    {
                    	n.keyvalues.addAll(rsibling.keyvalues);
                        n.data.addAll(rsibling.data);
                    }
                    else
                    {
                    	n.keyvalues.add(n.parent.keyvalues.get(index));
                    	n.keyvalues.addAll(rsibling.keyvalues);
                        n.children.addAll(rsibling.children);
                    }
                    n.parent.keyvalues.remove(index);
                    n.parent.children.remove(index+1);
                }
                //if(n.parent!=root)
               // System.out.print("****\n");
                checkValid(n.parent);
            }
        }
    }
    //Link the leaves together
    public void linkleaves()
    {
        BNode n=root;
        BNode prev=null;
        Queue<BNode> queue=new LinkedList<BNode>();
        queue.add(n);
        while(!queue.isEmpty())
        {
            BNode z=queue.element();
            for(int i=0;i<z.children.size();i++)
                queue.add(z.children.get(i));
            queue.remove();
            if(z.leaf)
            {
            	z.left=prev;
                if(linkleavesflag)
                {
                    start=z;
                    linkleavesflag=false;
                }
                if(!queue.isEmpty())
                z.right=queue.element();
                prev=z;
            }
        }
    }
    public void printsorted()
    {
        BNode temp=start;
        System.out.println("List is as follow ->");
        do{
            for(int i=0;i<temp.keyvalues.size();i++)
            {
                System.out.print(temp.keyvalues.get(i)+"--");
            }
            temp=temp.right;
        }while(temp!=null);
        System.out.println("\n------------------");
    }
    public String printinorder(BNode n,int tabs)
    {
        if(!n.leaf)
        {
            //tabs++;
            for(int k=0;k<n.children.size();k++)
            {
                printinorder(n.children.get(k), tabs+1);
                if(k<n.keyvalues.size())
                {
                    for(int j=0;j<tabs;j++)
                    {
                    	indexPrint.append("\t");
                    	System.out.print("\t");
                    }
                    indexPrint.append(n.keyvalues.get(k)+"\n");
                   // System.out.print("\t");
                    System.out.print(n.keyvalues.get(k)+"\n");
                }
            }
        }
        else
        {
            for(int i=0;i<n.keyvalues.size();i++)
            {
                for(int j=0;j<tabs;j++)
                {
                	 indexPrint.append("\t");
                System.out.print("\t");
                }
                indexPrint.append(n.keyvalues.get(i)+" ");
                System.out.print(n.keyvalues.get(i)+" ");
                if(n.leaf)
                {
                	indexPrint.append(n.data.get(i));
                System.out.print(n.data.get(i));
                }
                indexPrint.append("\n");
                System.out.print("\n");
            }
        }
        //System.out.print("\n");
        
        return indexPrint.toString();
    }
    
    
    //Print contents of the tree
    public void print(BNode n)
    {
        Queue<BNode> queue=new LinkedList<BNode>();
        queue.add(n);
        while(!queue.isEmpty())
        {
            BNode z=queue.element();
            if(z==root)
                System.out.println(""+z.keyvalues.size());
            for(int i=0;i<z.keyvalues.size();i++)
            {
                
                System.out.print("("+(z.keyvalues.get(i)));
                if(z.leaf)
                    System.out.print(","+(z.data.get(i)));
                System.out.print(")");
            }
            System.out.print(" & ");
            curval--;
            nextval+=z.keyvalues.size()+1;
            if(curval==0)
            {
                curval=nextval;
                nextval=0;
                System.out.println("\n-----------------\n");
            }
            if(z.leaf!=true)
            for(int i=0;i<z.children.size();i++)
                queue.add(z.children.get(i));
            queue.remove();
        }
    }
    public ArrayList numkeys(BNode n,Comparable x)
    {
        int total=0;
        int index=0;
        ArrayList<Integer> Res=new ArrayList<Integer>();
        try
        {
            for(int i=0;i<n.keyvalues.size();i++)
            {
                if(x.compareTo(n.keyvalues.get(i))==0)
                {
                    if(total==0)
                        index=i;
                    total++;
                }
                else if(x.compareTo(n.keyvalues.get(i))<0)
                {
                    break;
                }
                if(n.keyvalues.size()-1==i)
                {
                    if(n.right!=null)
                    {
                            i=-1;
                            n=n.right;
                    }
                }
            }
            
        }
        catch (Exception e)
	{
            e.printStackTrace();
	}
        Res.add(index);
        Res.add(total);
        return Res;
    }
    public ArrayList searchkey(BNode n,Comparable x,boolean flag)
    {
    	int index;
	    flag=true;
	    ArrayList<Comparable> flagIndexlist = new ArrayList<Comparable>();
	    try
	    {
		    for(index=0;index<n.keyvalues.size();index++)
		    {
			    if(x.compareTo(n.keyvalues.get(index))==0)
			    {
			    	flagIndexlist.add(index);
			    	flagIndexlist.add(1);
			    	return flagIndexlist;
			    }
			    if(x.compareTo(n.keyvalues.get(index))>0)
			    {
				    flag=false;
				    flagIndexlist.add(0);
			    	flagIndexlist.add(0);
			    	return flagIndexlist;
				    
			    }
			    if(n.keyvalues.size()-1==index)
			    {
				    if(n.right!=null)
				    {
					    index=-1;
					    n=n.right;
				    }
				    else
				    {
				    	flag=false;
					    flagIndexlist.add(0);
				    	flagIndexlist.add(0);
				    	return flagIndexlist;
				    }
			    }
		    }
	    }
	    catch (Exception e) 
	    {
			e.printStackTrace();
		}
	    flag=false;
	    flagIndexlist.add(0);
    	flagIndexlist.add(0);
    	return flagIndexlist;
    }

    /*
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            BPTree mytree=new BPTree();
            int key=0;
            String s;
            char inp;
            for(int i=1;i<=100;i++)
                mytree.insert(i, i);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            do
            {
                s=br.readLine();
                inp=s.charAt(0);
                switch(inp)
                {
                    case 'a':
                        key=Integer.parseInt(br.readLine());
                        mytree.insert(key, 0);
                        break;
                    case 'd':
                        key=Integer.parseInt(br.readLine());
                        mytree.delete(key);
                        break;
                    case 's':
                        mytree.printsorted();
                        break;
                    case 'i':
                        mytree.printinorder(mytree.root,0);
                        break;
                    default:continue;
                }
                
                //mytree.insert(key, i);
                mytree.curval=1;
                mytree.nextval=0;
                System.out.println("Tree after operation");
                mytree.print(mytree.root);
            }while(inp!='e');
            //mytree.print(mytree.root);
            //System.out.println("\nEnd of Insertion\n");
           // mytree.delete(99);
            //mytree.delete(20);
           // mytree.curval=1;
           // mytree.nextval=0;
           // System.out.println("Tree after deletion");
           // mytree.print(mytree.root);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        //mytree.delete(20);
       // mytree.curval=1;
       // mytree.nextval=0;
       // System.out.println("Tree after deletion");
       // mytree.print(mytree.root)
    }
*/

}
   /*
   class keydata implements Comparable<keydata>
   {
       public String key;
       int data;
       public keydata()
       {
        // System.out.print("s");
           data=0;
          // key=0;
       }
       public String getKey()
       {
           return key;
       }
       public int getData()
       {
           return data;
       }
       public void setKey(String keyval)
       {
           key=keyval;
       }
       public void setData(int dataval)
       {
           data=dataval;
       }
       public int compareTo(keydata o)
       {
           keydata k1=o;
           return key.compareTo(k1.key);
       }
    }*/