/**
* This program simulates the initial BTree up until the first split.
* It records the values through a RandomAccessFile.
* Each RandomAccessFile has data written in byte format
* Concepts related to byte is needed to understand the BTree values;
* @author Jeff Marion T. Andawi 2 BSMS CS
* @version December 11, 2017
*/
import java.io.*;
import java.util.*;
public class btdb {
	/**
	* The following are the variables used throughout the program
	* The RandomAccessFile are the ones that link the saved file to the program
	* The first subheadin, datavlues, are variables related to the datavlues
	* You can manipulate the size of each byte by editing the stringValue (which is used to seek)
	* Next is the databt subheading, which is used for the databt
	* Manipulating the order means manipulating the number of children a node can have
	* If you manipulate the order, you will need to compute for the nodeS sizes again.
	* Manipulating nodeValue is related to manipulating the node sizes. 
	*/
	RandomAccessFile datavalues;
	RandomAccessFile databt;
	// datavalues
	long recordCnt;
	int max = 8;
	int stringValue = 258;
	// databt
	int order = 5;
	int nodeValue = 112;
	int nodeSize = 14;
	int nodeMax = 16;
	long nodeCnt;
	long rootPos;
	/**
	* Starts the program to implement BTree
	* The first two input determines the files of the data.bt and data.values, which is not necessarily fixed
	* It uses HashMap to relate the keys to the record of the datavalues and the record of the datavalues to the actual String value
	* The while loop ensures that the program will accept input as long as the input "exit" isn't the input
	* Accepts the input in order to do what's needed
	*/
	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);
		String bt = args[0];
		String values = args[1];
		btdb b = new btdb();
		b.btFile(bt);
		b.valueFile(values);
		HashMap<Long, Integer> recKey = new HashMap<Long, Integer>();
		HashMap<Integer, String> recCount = new HashMap<Integer, String>();	
		int count = 0;
		while (true) {
			/**
			* Creates the base node, or the first root;
			* Writes the root node to the data.bt;
			*/
			if (b.getNodeCnt() == 0) {
				long[] z = b.createNode();
				b.writeBT(z);
			}
			// input related
			String container = sc.nextLine();
			String[] i = container.split("\\s+");
			// the key of the current input
			long key = Long.parseLong(i[2]);
			/**
			* Series of if statements to check what command the program will do
			* If the command is not a valid one, it will output an invalid output text and proceed to next iteration
			* If the command is insert, and it is a valid insert it will do the following: 
			* get the value to be inserted, put the value in the respective HashMaps, write to data.values and data.bt
			* If the current node is already greater than what must have, then the split function occurs.
			* The update command selects the HashMap related, updates it through series of methods, and rewrites the data.values
			* Each method related to writing, splitting and reading will be described in detail later on.
			* The select command just selects the associated value of the key.
			*/
			if(!i[1].equals("insert") && !i[1].equals("select") && !i[1].equals("exit") && !i[1].equals("update"))
				continue;
			else if (i[1].equals("exit"))
				break;
			else if (i[1].equals("insert") && key > 0 && !recKey.containsKey(key)) {
				String value = "";
				int length = i.length;
				if(length == 4) {
					value = i[3];
				}
				else {
					for(int o = 3; o < length; o++) {
						if(o == length - 1) {
							value = value + i[o];
						}
						else {
							value = value + i[o] + " ";
						}
					}
				}
				recKey.put(key,count);
				recCount.put(count,value);
				count++;
				b.writeValue(value);
				b.updateRCount();
				for(int z = 0; z <= b.getNodeCnt(); z++) {
					int k = 0;
					b.accessNode(z);
					long[] n = b.readBT();
					b.accessNode(z);
					for(int q = 2; q <= 11; q += 3) {
						if(n[q] == -1) {
							n[q] = key;
							n[q+1] = recKey.get(key);
							break;
						}
						else if(n[q] == -1) {
							k++;
						}
					}
					if(k == 4) {
						long[] r = b.createNode();
						long[] v = b.createNode();
						b.splitBT(n,r,v,key,recKey.get(key));
						b.updateRootBT();
						b.sortBT(n);
						b.sortBT(r);
						b.sortBT(v);
						b.accessNode(z);
						b.writeBT(n);
						b.writeBT(r);
						b.writeBT(v);
						break;
					}
					b.sortBT(n);
					b.writeBT(n);
					b.moveBTMax();
				}
				System.out.printf("< %d inserted.\n", key);
			}
			else if(i[1].equals("update") && recKey.containsKey(key)) {
				String value = "";
				int length = i.length;
				if(length == 4) {
					value = i[3];
				}
				else {
					for(int o = 3; o < length; o++) {
						if(o == length - 1) {
							value = value + i[o];
						}
						else {
							value = value + i[o] + " ";
						}
					}
				}
				int temp = recKey.get(key);
				recCount.put(temp, value);
				b.accessValue(temp);
				b.writeValue(value);
				b.movePointerMax();
				System.out.printf("< %d updated.\n", key);
			}
			else if(i[1].equals("select") && recKey.containsKey(key)) {
				int a = recKey.get(key);
				b.accessValue(a);
				String sel = b.readValue();
				b.movePointerMax();
				System.out.printf("< %d => %s\n", key, sel);
			}
			else if(!recKey.containsKey(key)) {
				System.out.printf("ERROR: %d does not exist.\n", key);
			}
			else if(recKey.containsKey(key)) {
				System.out.printf("ERROR: %d already exists.\n", key);
			}
			else if(i[1].equals("exit")) {
				b.closeFile();
				break;
			}
			else {
				System.out.println("ERROR: invalid command.");
			}
		}
	}
	/**
	* Returns the order of the BTree.
	* The order determines how many children a BTree can have.
	* No parameters needed.
	* @return order, which is the order of the BTree
	*/
	public int returnOrder() throws IOException {
		return order;
	}
	/**
	* Creates the Data.values File (or the equivalent of it)
	* The data.values file is determined by the String input
	* If it doesn't exist, it will create the File as an RandomAccessFile with a read and write option
	* If it exists, it associates it to our variable datavalues which will be manipulated later on. It will also read the recordCount in order to know the inputs
	* @param String s which is the filename
	*/
	public void valueFile(String s) throws IOException {
		File f = new File(s);
		if(!f.exists()) {
			recordCnt = 0;
			datavalues = new RandomAccessFile(f, "rwd");
			datavalues.seek(0);
			datavalues.writeLong(recordCnt);
		}
		else {
			datavalues = new RandomAccessFile(f, "rwd");
			datavalues.seek(0);
			recordCnt = datavalues.readLong();
		}
	}
	/**
	* Creates the Data.bt File (or the equivalent of it)
	* It is used to contain the necessary nodes, which will determine our Btree
	* The data.bt file is determined by the String input
	* If it doesn't exist, it will create the File as an RandomAccessFile with a read and write option
	* If it exists, it associates it to our variable datavalues which will be manipulated later on. It will also read the recordCount in order to know the inputs
	* @param String s which is the filename
	*/	
	public void btFile(String s) throws IOException {
		File f = new File(s);
		if(!f.exists()) {
			nodeCnt = 0;
			rootPos = 0;
			databt = new RandomAccessFile(f, "rwd");
			databt.seek(0);
			databt.writeLong(nodeCnt);
			databt.writeLong(rootPos);
		}
		else {
			databt = new RandomAccessFile(f, "rwd");
			databt.seek(0);
			nodeCnt = databt.readLong();
			rootPos = databt.readLong();
		}
	}
	/**
	* Updates the count of the records in data.values
	* It points back at the start, where the recordCount is contained.
	* It then overwrites it and goes back to its original pointer
	*/
	public void updateRCount () throws IOException {
		recordCnt++;
		long temp = datavalues.getFilePointer();
		datavalues.seek(0);
		datavalues.writeLong(recordCnt);
		datavalues.seek(temp);
	}
	/**
	* Moves to the desired record in data.values
	* @param int i, where i is the record Offset
	*/	
	public void accessValue (int i) throws IOException {
		datavalues.seek(8 + i*258);
	}
	/**
	* Writes (or inserts) the record to the data.values
	* It starts by converting the String s into a byte[]
	* It writes first the length as short before writing the byte[] itself.
	* Afterwards, it goes back to the original pointer
	* @param String s, which is the string to be written inside
	*/			
	public void writeValue(String s) throws IOException {
		byte[] byteArray = s.getBytes("UTF-8");
		datavalues.writeShort((short) byteArray.length);
		datavalues.write(byteArray);
		max += stringValue;
		datavalues.seek(max);
	}
	/**
	* Moves the pointer of the data.values back to original
	* Used when reading/rewriting the data.values
	*/
	public void movePointerMax() throws IOException {
		datavalues.seek(max);
	}
	/**
	* Reads the data.values record
	* It starts by reading the length of the value
	* Next is using the length for the byte[] array which will be used to contain the value read in data.values
	* Afterwards, it will be converted into String using proper String constructor
	* @return S, the string from byte[]
	*/	
	public String readValue () throws IOException {
		short len = datavalues.readShort();
		byte[] byteArray = new byte[len];
		datavalues.read(byteArray);
		String s = new String(byteArray, "UTF-8");
		return s;
	}
	/**
	* This creates the node needed, and the initial values
	* The initial values are -1, as specified
	* These values will be replaced in the following methods.
	* This will also update the nodeCount;
	* return node, which is the node created
	*/	
	public long[] createNode() throws IOException {
		long[] node = new long[nodeSize];
		for(int g = 0; g < nodeSize; g++)
			node[g] = -1;
		nodeMax += nodeValue;
		return node;
	}
	/**
	* Moves to the desired record in data.bt
	* @param int i, where i is the node number
	*/		
	public void accessNode(int i) throws IOException {
		databt.seek(16 + (i * 112));
	}
	/**
	* A series of complicated instructions to split the node.
	* It starts by making an array holder temporary. It is to contain the key values and the offsets
	* Afterwards, the array will be sorted to determine the median.
	* After sorting, the nodes will be reset to -1, just like initially creating a node
	* After resetting the nodes, it will assign the values needed
	* These values to be inserted are determined by the lower/greater than the median
	*/
	public void splitBT (long[] n, long[] v, long[] r, long key, int offset) throws IOException {
		long[] keyTemp = new long[5];
		long[] offsetTemp = new long[5];
		int cnter = 2;
		for(int m = 0; m < 4; m++) {
			keyTemp[m] = n[cnter];
			offsetTemp[m] = n[cnter+1];
			cnter += 3;
		}
		keyTemp[4] = key;
		offsetTemp[4] = offset;
		for(int f = 0; f < order-1; f++) {
			for(int e = 1; e < (4-f); e++) {
				if(keyTemp[e-1] > keyTemp[e]) {
					long temp = keyTemp[e-1];
					long temp2 = offsetTemp[e-1];
					keyTemp[e-1] = keyTemp[e];
					keyTemp[e] = temp;
					offsetTemp[e-1] = offsetTemp[e];
					offsetTemp[e] = temp2;
				}
			}
		}
		for(int f = 0; f < nodeSize; f++) {
			n[f] = -1;
			v[f] = -1;
			r[f] = -1;
		}
		long medianKey = keyTemp[(int) Math.ceil(order/2)];
		long medianOffset = keyTemp[(int) Math.ceil(order/2)];
		n[2] = medianKey;
		n[3] = medianOffset;
		int x = 0;
		v[1] = nodeCnt;
		r[1] = nodeCnt;
		for(int g = 2; g <= 5; g += 3) {
			v[g] = keyTemp[0+x];
			v[g+1] = keyTemp[1+x];
			r[g] = keyTemp[3+x];
			r[g+1] = keyTemp[4+x];
			x++;
		}
	}
	/**
	* Moves the pointer of the data.bt back to original
	* Used when reading/rewriting the data.bt
	*/	
	public void moveBTMax() throws IOException {
		databt.seek(nodeMax);
	}
	/**
	* Updates the root in data.bt
	* It points back at the start, where the root is contained.
	* It goes to 8 since the first (0) is the number of nodes in the data.bt
	* It then overwrites it and goes back to its original pointer
	*/	
	public void updateRootBT() throws IOException {
		rootPos += 2;
		long temp = databt.getFilePointer();
		databt.seek(8);
		databt.writeLong(rootPos);
		databt.seek(temp);
	}
	/**
	* Sorts the node whenever a new key is inserted
	* Sorts the key and the offset associated in the record
	*/		
	public void sortBT(long[] n) throws IOException {
		for(int u = 2; u <= 8; u += 3) {
			if(n[u] > n[u+3]) {
				int temp = (int) n[u];
				int temp2 = (int) n[u+1];
				n[u] = n[u+3];
				n[u+1] = n[u+4];
				n[u+3] = temp;
				n[u+4] = temp2;
			}
		}
	}
	/**
	* Writes (or inserts) the record to the data.bt
	* It writes as long, so each entry is 8 bytes
	* @param long[] a, which is the array that contains the values to be written
	*/		
	public void writeBT(long[] a) throws IOException {
		for(int k = 0; k < nodeSize; k++) {
			databt.writeLong(a[k]);
		}
		nodeMax += 112;
		databt.seek(nodeMax);
	}
	/**
	* returns the nodeCount of the data.bt
	*/		
	public long getNodeCnt() {
		return nodeCnt;
	}
	/**
	* Reads the data.bt node
	* It starts by making the array which will contain each value (the array is long)
	* Next is for each iteration, determined by nodeSize, it will read the data.bt as long
	* @return s, the array long[]
	*/		
	public long[] readBT() throws IOException {
		long temp = databt.getFilePointer();
		long[] r = new long[nodeSize];
		for(int l = 0; l < nodeSize; l++) {
			r[l] = databt.readLong();
		}
		return r;
	}
	/**
	* Updates the nodeCount, which is when a split happens.
	* It goes back to the origin which is 0, overwrites it, and goes back to where it came from
	* It writes as long since it was specified that nodeCount is Long.
	*/		
	public void updateNCount () throws IOException {
		nodeCnt++;
		long temp = databt.getFilePointer();
		databt.seek(0);
		databt.writeLong(recordCnt);
		databt.seek(temp);
	}
	/**
	* Closes the RandomAccessFile, in order to save properly
	* Done at the end, when the command is exit
	*/		
	public void closeFile() throws IOException {
		databt.close();
		datavalues.close();
	}	
}