package testing;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.*;

class createNodeTest {

	@Test
	void test() throws IOException {
		btdbTesting test = new btdbTesting();
		int[] node = new int[14];	
		for(int g = 0; g < 14; g++)
			node[g] = -1;
		int[] output = test.createNode();
		assertEquals(Arrays.toString(node), Arrays.toString(output));
	}

}
