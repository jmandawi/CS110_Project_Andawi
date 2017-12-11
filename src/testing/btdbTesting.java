package testing;
import java.io.*;
import java.util.*;
public class btdbTesting {
	RandomAccessFile datavalues;
	RandomAccessFile databt;
	// datavalues
	int recordCnt = 0;
	int max = 8;
	int stringValue = 258;
	// databt
	int order = 5;
	int nodeValue = 112;
	int nodeSize = 14;
	int nodeMax = 16;
	long nodeCnt;
	long rootPos;
	
	public int returnOrder() throws IOException {
		return order;
	}
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
			recordCnt = datavalues.readInt();
		}
	}
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
	public int updateRCount () throws IOException {
		recordCnt++;
		long temp = datavalues.getFilePointer();
		datavalues.seek(0);
		datavalues.writeLong(recordCnt);
		datavalues.seek(temp);
		return recordCnt;
	}
	public int accessValue (int i) throws IOException {
		int t = 8 + (i * 258);
		return t;
	}		
	public byte[] writeValue(String s) throws IOException {
		byte[] byteArray = s.getBytes("UTF-8");
		return byteArray;
	}
	public void movePointerMax() throws IOException {
		datavalues.seek(max);
	}
	public String readValue () throws IOException {
		short len = datavalues.readShort();
		byte[] byteArray = new byte[len];
		datavalues.read(byteArray);
		String s = new String(byteArray, "UTF-8");
		return s;
	}
	public int[] createNode() throws IOException {
		int[] node = new int[nodeSize];
		for(int g = 0; g < nodeSize; g++)
			node[g] = -1;
		nodeMax += nodeValue;
		return node;
	}
	public void accessNode(int i) throws IOException {
		databt.seek(16 + (i * 112));
	}
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
	public void moveBTMax() throws IOException {
		databt.seek(nodeMax);
	}
	public void updateRootBT() throws IOException {
		rootPos += 2;
		long temp = databt.getFilePointer();
		databt.seek(8);
		databt.writeLong(rootPos);
		databt.seek(temp);
	}
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
	public void writeBT(long[] a) throws IOException {
		for(int k = 0; k < nodeSize; k++) {
			databt.writeLong(a[k]);
		}
		nodeMax += 112;
		databt.seek(nodeMax);
	}
	public long getNodeCnt() {
		return nodeCnt;
	}
	public long[] readBT() throws IOException {
		long temp = databt.getFilePointer();
		long[] r = new long[nodeSize];
		for(int l = 0; l < nodeSize; l++) {
			r[l] = databt.readLong();
		}
		return r;
	}
	public void updateNCount () throws IOException {
		nodeCnt++;
		long temp = databt.getFilePointer();
		databt.seek(0);
		databt.writeLong(recordCnt);
		databt.seek(temp);
	}
	public void closeFile() throws IOException {
		databt.close();
		datavalues.close();
	}	
}