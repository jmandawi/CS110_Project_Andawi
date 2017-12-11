package testing;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class recordTest {

	@Test
	void test() throws IOException {
		btdbTesting test = new btdbTesting();
		int output = test.updateRCount();
		assertEquals(1, output);
	}

}
