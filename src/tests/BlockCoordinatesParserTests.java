package tests;
import org.junit.Test;

import internal.BlockCoordinatesParser;

import java.io.IOException;

import org.junit.Before;
/**
 * 
 * @author Anthony Rathe
 *
 */
public class BlockCoordinatesParserTests {
	
	BlockCoordinatesParser parser;
	String path;
	
	@Before
	public void setup() {
		path = "src/internal/blockCoordinates.txt";
		parser = new BlockCoordinatesParser(path);
	}
	
	@Test
	public void multipleLinesOfFloatsTest() throws IOException {
		parser.getCoordinates();
	}
	
}
