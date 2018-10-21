import static java.lang.System.err;
import static java.lang.System.out;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
/*
 * 
 * https://www.javaworld.com/article/2073446/screen-snapshots-with-java-s-robot.html
 */
import javax.imageio.ImageIO;

/**
 * Simple class demonstrating the general utility of the java.awt.Robot class in
 * capturing screen snapshots.
 *
 * @author Dustin
 */
public class JavaRobotExample {
	/**
	 * Collection of informal file format names understood by registered
	 * readers.
	 */
	private final static List<String> INFORMAL_FILE_FORMAT_NAMES;

	/** Operating system independent new line. */
	private final static String NEW_LINE = System.getProperty("line.separator");

	/** Default delay in milliseconds. */
	private final static int DEFAULT_DELAY_MS = 2500;

	/** Default filename base when no filename base is provided. */
	private final static String DEFAULT_FILE_NAME_BASE = "screenshot";

	/**
	 * Default file format for generated image file used when none is provided.
	 */
	private final static String DEFAULT_INFORMAL_FILE_FORMAT_NAME;

	static {
		INFORMAL_FILE_FORMAT_NAMES = Arrays.asList(ImageIO.getReaderFormatNames());
		DEFAULT_INFORMAL_FILE_FORMAT_NAME = isFormatRecognized("png") ? "png" : provideSingleInformalFileFormatName();
	}

	/**
	 * Capture snaphot of the current screen and write it to an output file with
	 * the provided file name.
	 *
	 * @param newFileName
	 *            Name of file (without prefix) to which screen snapshot should
	 *            be written.
	 * @param newFileFormat
	 *            The informal file format name to be used to specify the format
	 *            of the file the screen snapshot is written to.
	 * @param newDelayInMs
	 *            Delay (measured in milliseconds) that should be employed
	 *            before taking screen snapshot to allow screen adjustment (such
	 *            as closing the terminal in which this application is
	 *            executed).
	 */
	public static void captureScreenShot(final String newFileName, final String newFileFormat, final int newDelayInMs) {
		final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		final Rectangle screenRectangle = new Rectangle(screenDimension);
		try {
			final Robot robot = new Robot();
			robot.delay(newDelayInMs);
			final BufferedImage screenImage = robot.createScreenCapture(screenRectangle);
			ImageIO.write(screenImage, newFileFormat, new File(newFileName));
		} catch (AWTException awtEx) {
			if (System.console() == null) {
				err.println("Not supported for headless console - " + awtEx.toString());
			} else {
				err.println("Not supported for this environment - " + awtEx.toString());
			}
		} catch (IOException ioEx) {
			err.println("Unable to write screen shot to file " + newFileName + " - " + ioEx.toString());
		}
	}

	/** Print the supported informal file format names to standard output. */
	public static void printInformalFileFormatNames() {
		for (final String formatName : INFORMAL_FILE_FORMAT_NAMES) {
			out.println(formatName);
		}
	}

	/**
	 * Provide a single informal file format name that is supported by my
	 * readers.
	 *
	 * @return Single informal file format name support by my readers; may be
	 *         empty String if I don't have any valid informal file format
	 *         names.
	 */
	public static String provideSingleInformalFileFormatName() {
		return INFORMAL_FILE_FORMAT_NAMES != null && !INFORMAL_FILE_FORMAT_NAMES.isEmpty()
				? INFORMAL_FILE_FORMAT_NAMES.get(0) : "";
	}

	/**
	 * Indicates whether the provided String format represents a valid informal
	 * file format name for my registered readers.
	 *
	 * @param candidateInformalFormatName
	 *            String of possible file format.
	 * @return {@code true} if the provided String represents a valid informal
	 *         file format name for my registered readers.
	 */
	public static boolean isFormatRecognized(final String candidateInformalFormatName) {
		return INFORMAL_FILE_FORMAT_NAMES.contains(candidateInformalFormatName);
	}

	/**
	 * Extracts file name and format information from the provided arguments,
	 * assumed to have come from the command-line or other source. It is also
	 * assumed that the provided array of Strings has two elements with the
	 * first String representing the base of the file name (no extension) and
	 * the second representing the file format. If either the base file name or
	 * the format is not specified, defaults are used for both file name and
	 * format.
	 *
	 * @param arguments
	 *            Strings with first String representing the base file name (no
	 *            extension) and the second String representing the file format.
	 * @return Two Strings with the first String representing the file name to
	 *         be written to (without extension and may be the same as provided)
	 *         and the second String representing a valid informal file format
	 *         name.
	 */
	public static String[] extractFileNameAndFileFormat(final String[] arguments) {
		String fileNameBase;
		String fileFormat;
		if (arguments.length < 2) {
			out.println("If specified arguments are to be used, both must be specified." + NEW_LINE);
			out.println("Because either file name or file format was not specified, " + "defaults are used for both ('"
					+ DEFAULT_FILE_NAME_BASE + "' for the generated base filename of file format "
					+ DEFAULT_INFORMAL_FILE_FORMAT_NAME + " with delay of " + DEFAULT_DELAY_MS + ")." + NEW_LINE
					+ NEW_LINE + "To explicitly specify them, provide them as command-line arguments:" + NEW_LINE
					+ "     dustin.examples.JavaRobotExample <<file_name_base>> <<file_format>> <<delay_ms>>" + NEW_LINE
					+ NEW_LINE + "where file_name_base is name of generated image file without its suffix "
					+ "and file_format is the image format ('PNG', 'GIF', 'JPG', etc.).");
			fileNameBase = DEFAULT_FILE_NAME_BASE;
			fileFormat = DEFAULT_INFORMAL_FILE_FORMAT_NAME;
		} else {
			fileNameBase = arguments[0];
			final String candidateFileFormat = arguments[1];
			fileFormat = isFormatRecognized(candidateFileFormat) ? candidateFileFormat
					: DEFAULT_INFORMAL_FILE_FORMAT_NAME;
		}
		return new String[] { fileNameBase, fileFormat };
	}

	/**
	 * Extract the delay in milliseconds from the provided arguments.
	 *
	 * @param arguments
	 *            Arguments, most likely from the command line, that are
	 *            expected to include the number of milliseconds of delay before
	 *            screen capture as the third argument.
	 * @return The extract delay in milliseconds before screen capture should
	 *         take place.
	 */
	public static int extractDelayInMilliseconds(final String[] arguments) {
		final int defaultDelayInMs = DEFAULT_DELAY_MS;
		int requestedDelayInMs;
		if (arguments.length > 2) {
			try {
				requestedDelayInMs = Integer.valueOf(arguments[2]);
				if (requestedDelayInMs < 0 || requestedDelayInMs > 60000) {
					requestedDelayInMs = defaultDelayInMs;
					err.println("Specified delay of " + requestedDelayInMs
							+ " is NOT between 0 and 60000 ms; setting to " + defaultDelayInMs);
				}
			} catch (NumberFormatException nfe) {
				requestedDelayInMs = defaultDelayInMs;
				err.println(arguments[2] + " is not a valid number of milliseconds for a delay.");
			}
		} else {
			requestedDelayInMs = defaultDelayInMs;
		}
		return requestedDelayInMs;
	}

	/**
	 * Main executable method.
	 * 
	 * @param arguments
	 *            Command-line arguments: none expected.
	 */
	public static void main(final String[] arguments) {
		final String[] fileNameAndFormat = extractFileNameAndFileFormat(arguments);
		final String fileFormat = fileNameAndFormat[1];
		final String fileName = fileNameAndFormat[0] + "." + fileFormat.toLowerCase();
		final int delayInMs = extractDelayInMilliseconds(arguments);
		captureScreenShot(fileName, fileFormat, delayInMs);
	}
}