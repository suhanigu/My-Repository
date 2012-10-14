/**
 * 
 */


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import BTree.*;

/**
 * @author Nishant Vardhan
 *
 */
public class Index 
{
	String fileName = null;
	String indexName = null;
	String column = null;
	boolean pubflag=true;
	BPTree bpTree = new BPTree();
	
	

	public Index()
	{
		
	}
	
	public Index(String column,String indexName, String filename) 
	{
		this.fileName = fileName;
		this.indexName = indexName;
		this.column = column;
		bpTree = new BPTree();
	}

	public void dumpIndex()
	{
		ObjectOutputStream outputFileObj = null;
		String indexFile = null;

		try
		{
			
			if(fileName!=null && !fileName.equals("") && indexName!=null && !indexName.equals(""))
			{
				indexFile = fileName+"."+indexName;
				outputFileObj = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(indexFile)));
				outputFileObj.writeObject(column);
				outputFileObj.writeObject(bpTree);
				outputFileObj.close();
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
		
	}
	
	public String viewIndex() 
	{
		String printIndex="";
		bpTree.indexPrint = new StringBuffer();
		bpTree.indexPrint.append("Index "+indexName+" over column "+column+"\n\n");
		printIndex=bpTree.printinorder(bpTree.root, 0);		
		System.out.println("printIndex = \n"+printIndex);
		return printIndex;
	}
	
	public Iterator<Integer> iterator(String key)
	{
	
		IndexIterator indexiterator = new IndexIterator(key);
				
		
		return indexiterator;
	}
	
	public void insertIntoBPTree(String recordvalue, int recordId)
	{
		bpTree.insert(recordvalue, recordId);
	}
	
	private class IndexIterator implements Iterator<Integer>
	{
		Comparable k;
		boolean privflag;
		//static boolean pubflag;
		int kindex;
		int index;
		int nextdata;
		int flagValue=0;
		boolean hasnext, flag;
                int totRecords;
		ArrayList<Comparable> flagIndexlist = new ArrayList<Comparable>();
                ArrayList<Integer> Indexlengthlist = new ArrayList<Integer>();
		BNode n;
			public IndexIterator(Comparable key) {
			// TODO Auto-generated constructor stub
				k=key;
				n=bpTree.search(k);
             while(n.left!=null &&  n.left.keyvalues.get(n.left.keyvalues.size()-1)==k )
            	 n=n.left;
                                Indexlengthlist=bpTree.numkeys(n, k);
                                totRecords=Indexlengthlist.get(1);
                                index=Indexlengthlist.get(0);
				/*flagIndexlist=bpTree.searchkey(n, k, flag);
				index = (Integer) flagIndexlist.get(0);
				flagValue = (Integer) flagIndexlist.get(1);
				if(flagValue==1)
					flag = true;
				else
					flag = false;*/
				kindex=0;
				hasnext=true;
				privflag=pubflag;
			}
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			//int index=bpTree.findindex(n.keyvalues, k);
			/*if(( (((index+kindex)<n.keyvalues.size()-1)&&(n.keyvalues.get(index+kindex+1)==k)) || ((n.right!=null)&&(k==n.right.keyvalues.get(0))))&& hasnext && flag)
				return true;
			else
				return false;
		*/
                    if(kindex<totRecords)
                        return true;
                    else
                        return false;
		}
		
		
	/*	public Node searchindex(String key)
		{
		
			tree.search(String key);
			return Node;
		}*/
		@Override
		public Integer next() 
		{
			try
			{
				//int index=bpTree.findindex(n.keyvalues, k);
				if(privflag!=pubflag)
					throw new ConcurrentModificationException();
			/*	if((index+kindex)<(n.keyvalues.size()-1))
	            {
					return n.data.get(index+(++kindex));
	            }
				else 
				{
					 n= n.right;
					 //key= n.keyvalues.get(0);
					 index=0;
					 kindex=0;
					 return n.data.get(0);
				}
                         *
                         */
                                if(index<n.keyvalues.size())
                                {
                                    kindex++;
                                    return n.data.get(index++);

                                }
                                else
                                {
                                    n=n.right;
                                    index=0;
                                    kindex++;
                                    return n.data.get(index++);
                                }
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				return 0;
			}
		}

		@Override
		public void remove() 
		{
			try
			{
				if(privflag!=pubflag)
					throw new ConcurrentModificationException();
				pubflag=false;
				privflag=false;

				//hasnext=hasNext();
                //                if(hasnext)
				//{
					if(index<=(n.keyvalues.size()-1))
						nextdata=n.data.get(index);
					else
						nextdata=n.right.data.get(0);
				//}
				if(index>0)
				{
					if(k!=n.keyvalues.get(index-1))
						System.out.println("lolnoobs");
					else
						System.out.println("yoyo"+k);
                n.keyvalues.remove(index-1);
				n.data.remove(index-1);
				bpTree.checkValid(n);
				}
				else
				{
					if(k!=n.left.keyvalues.get(n.left.keyvalues.size()-1))
						System.out.println("lolnoobs");
					else
						System.out.println("yoyo");
	                n.left.keyvalues.remove(n.left.keyvalues.size()-1);
					n.left.data.remove(n.left.data.size()-1);
					bpTree.checkValid(n.left);
				}
				
				bpTree.linkleaves();
                                if(hasnext)
                                {
                                    n=bpTree.search(k);
                                    while(n.left!=null &&  n.left.keyvalues.get(n.left.keyvalues.size()-1)==k )
                                   	 n=n.left;
                                    Indexlengthlist=bpTree.numkeys(n, k);
                                    totRecords=Indexlengthlist.get(1);
                                    index=Indexlengthlist.get(0);
                                    kindex=0;
                                    while(n.data.get(index)!=nextdata)
                                    {
                                        if(index==n.data.size()-1)
                                        {
                                                n=n.right;
                                                index=0;
                                        }
                                        else
                                                index++;
                                        kindex++;
                                    }
				}
                                
				/*if(hasnext)
				{
					if((index+kindex)<(n.keyvalues.size()-1))
						nextdata=n.data.get(index+kindex+1);
					else
						nextdata=n.right.data.get(0);
				}
				n.keyvalues.remove(index+kindex);
				n.data.remove(index+kindex);
				bpTree.checkValid(n);
				bpTree.linkleaves();
				if(hasnext)
				{
					n=bpTree.search(k);
					flagIndexlist=bpTree.searchkey(n, k, flag);
					index = (Integer) flagIndexlist.get(0);
					flagValue = (Integer) flagIndexlist.get(1);
					if(flagValue==1)
						flag = true;
					else
						flag = false;
					kindex=0;
					while(n.data.get(index)!=nextdata)
					{
						if(index==n.data.size()-1)
						{
							n=n.right;
							index=0;
						}
						else
							index++;
					}
				}
                                 * 
                                 */
				//bpTree.delete(k);
				
				// TODO Auto-generated method stub
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
		}
		
	}

}
