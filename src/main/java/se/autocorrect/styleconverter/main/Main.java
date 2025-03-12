/*-
 *  
 * style-converter
 *  
 * Copyright (C) 2025 Autocorrect Design HB
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *  
 */
package se.autocorrect.styleconverter.main;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.oscim.theme.ThemeFile;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import se.autocorrect.styleconverter.StyleConverter;
import se.autocorrect.styleconverter.util.Try;

@Command(name = "json2xml", mixinStandardHelpOptions = true, showAtFileInUsageHelp = false,
version = {"first draft 0.0.1", "picocli " + CommandLine.VERSION},
header = "Convert JSON style data to XML",
description = {
        "The program provides a conversion mechanism for JSON styles to XML."
	},
	optionListHeading = "COMMAND-LINE Options%n"
)
public class Main implements Callable<Integer> {

	// TODO: Add this option with handling in the near future
//	@Option(names = { "-s", "--spritesdir" }, description = { "Sprite directory location",
//			"Specifies the location of potential sprites extracted from the style provided.",
//			"If no location is set \".sprites\" under the user home directory will be used." })
//	String spriteDirLocation;

	@Option(names = { "-f", "--file" }, description = { "The input file",
			"Specifies the input file. ", }, required = true)
	String inputFile;

	@Option(names = { "-o", "--out" }, description = { "The output file", "Specifies the output file.",
			"If no outputfile is specified \"out.xml\" will be used and placed in the user home directory." })
	String outputFile;

	@Option(names = { "-h", "--help", "-?", "-help" }, usageHelp = true, description = "Display this help and exit")
	private boolean help;

	public static void main(String[] args) {
		System.exit(new CommandLine(new Main()).execute(args));
	}

	@Override
	public Integer call() throws Exception {

		int retCode = 0;
		
		Try<InputStream> tInputStream = Try.of(() -> new FileInputStream(new File(inputFile)));

		if(tInputStream.isSuccess()) {
			
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tInputStream.get()))) {

				String json = bufferedReader.lines().collect(Collectors.joining("\n"));

				StyleConverter converter = StyleConverter.getDefaultJsonConvert();
				ThemeFile result = converter.convert(json);

				String out = outputFile != null ? outputFile : System.getProperty("user.home") + "/out.xml";
				File targetFile = new File(out);

				try (InputStream themeAsStream = result.getRenderThemeAsStream()) {

					Files.copy(themeAsStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}

		} else {

			System.err.println("Could not read file: " + inputFile);
			retCode = -1;
		}

		return retCode;
	}
}
