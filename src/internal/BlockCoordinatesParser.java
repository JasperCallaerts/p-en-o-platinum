package internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 
 * @author Anthony Rathe
 *
 */
public class BlockCoordinatesParser {
	
	public BlockCoordinatesParser(String path) {
		
		this.path = Paths.get(path);
		
	}
	
	public Path getPath() {
		return this.path;
	}
	
	public List<Vector> getCoordinates() throws IOException {
		List<Vector> coordinates = new ArrayList<Vector>();
		List<String> coordinateStrings = new ArrayList<String>();
		try {
			Stream<String> coordinateStream = Files.lines(getPath(), StandardCharsets.UTF_8);
			coordinateStream.forEach(coordinateStrings::add);
			try {
				for (String coordinateString : coordinateStrings) {
					String[] splitString = coordinateString.split("\\s+");
					float xValue = Float.parseFloat(splitString[0]);
					float yValue = Float.parseFloat(splitString[1]);
					float zValue = Float.parseFloat(splitString[2]);
					coordinates.add(new Vector(xValue, yValue, zValue));
				}
			}catch(Exception exception) {
				coordinateStream.close();
				throw new IOException("Something was wrong with the given coordinates file: ", exception);
			}
			coordinateStream.close();
			return coordinates;
		}catch (NoSuchFileException exception){
			throw new IllegalArgumentException("The given file path does not exist.", exception);
		}
		
		
	}
	
	private final Path path;
}
