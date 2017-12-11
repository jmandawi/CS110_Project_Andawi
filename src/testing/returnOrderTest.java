package testing;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class returnOrderTest {

	@Test
	void test() throws IOException {
		btdbTesting test = new btdbTesting();
		int output = test.returnOrder();
		assertEquals(5, output);
	}

}
