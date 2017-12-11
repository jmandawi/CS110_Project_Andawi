package testing;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class accessRecordTest {

	@Test
	void test() throws IOException {
		btdbTesting test = new btdbTesting();
		int output = test.accessValue(0);
		assertEquals(8, output);
	}

}
