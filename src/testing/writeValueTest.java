package testing;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.*;

class writeValueTest {

	@Test
	void test() throws IOException {
		btdbTesting test = new btdbTesting();
		String s = "LOL";
		byte[] byteArray = s.getBytes("UTF-8");
		byte[] output = test.writeValue(s);
		assertEquals(Arrays.toString(byteArray),Arrays.toString(output));
	}

}
