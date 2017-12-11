package testing;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class valueFileTest {

	@Test
	void test() throws IOException {
		btdbTesting test = new btdbTesting();
		test.valueFile("Data.values");
		String dir = System.getProperty("user.dir");
		File temp = new File(dir);
		assertTrue(temp.exists());
	}

}
