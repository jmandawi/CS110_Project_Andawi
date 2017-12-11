package testing;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class btFileTest {

	@Test
	void test() throws IOException {
		btdbTesting test = new btdbTesting();
		test.valueFile("Data.bt");
		String dir = System.getProperty("user.dir");
		File temp = new File(dir);
		assertTrue(temp.exists());
	}

}
