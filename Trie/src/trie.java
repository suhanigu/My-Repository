import java.util.ArrayList;


public class trie {

	public static class TrieNode{
		char key;
		int value;
		ArrayList<TrieNode> children = new ArrayList<TrieNode>();
		ArrayList<TrieNode> next = new ArrayList<TrieNode>();
		
		public TrieNode(char keyentry){
			key = keyentry;
		}
		
		public TrieNode(char keyentry, int v){
			key = keyentry;
			value = v;
		}
	}
	
	public static TrieNode root;
	
	public static void add(TrieNode root, String keyentry, int v){
		int i = 0;
		int j = 0;
		int flag=0;
		char[] key = keyentry.toCharArray();
		if(root!=null){
			while(j < key.length){
				if(!root.children.isEmpty()){
					while(i < root.children.size()){
					if(root.children.get(i).key == key[j]){
						root = root.children.get(i);
						j++;
						flag = 1;
						break;
					}
					i++;
				}
				}
				if(flag == 0)
					break;
			}
			j = 0;
			while(j < key.length){
				TrieNode node = new TrieNode(key[j]);
				root.children.add(node);
				root = root.children.get(root.children.size() - 1);
				j++;
			}
			TrieNode node = new TrieNode('\0', v);
			root.children.add(node);
			root = root.children.get(root.children.size() - 1);
		}
		else{
			j = 0;
			while(j < key.length){
				TrieNode node = new TrieNode(key[j]);
				root.children.add(node);
				root = root.children.get(root.children.size() - 1);
				j++;
			}
			TrieNode node = new TrieNode('\0', v);
			root.children.add(node);
			root = root.children.get(root.children.size() - 1);
		}
	}
	
	public static void isPresent(TrieNode root, String keyentry){
		int i = 0;
		int j = 0;
		char[] key = keyentry.toCharArray();
		while(i < root.children.size() && j<key.length){
			if(root.children.get(i).key == key[j]){
				//System.out.printf("%c", key[j]);
				j++;
				root = root.children.get(i);
				i=0;
			}
			else{
				i++;
			}
		}
		
		if(j==key.length)
			System.out.println(keyentry + " is present");
		else
			System.out.println(keyentry + " not present");
	}
	
	public static void main(String[] args){
		root = new TrieNode('\0', 0);
		add(root, "amy" , 56);
		add(root, "suhani", 26);
		isPresent(root, "amy");
		isPresent(root, "suhani");
		isPresent(root, "foo");
	}
}

