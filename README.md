# style-converter
An experimental style to themes converter for VTM/Mapsforge. 

## Introduction
This converter does not, quite naturally, cover all cases of the style definitions possible in the JSON format. However, maybe 80%? Which in some cases is good enough. There are, of course, many things that can be improved in it, there are currently several limitations. This is just a first draft version for experimentation and review. 

Please feel free to take it for a run and suggest improvements you might immediately see. 

## Status
Below follows a list of items that can be improved: 
* there are several cases of code duplication, some of which are low hanging fruit that can be picked asap
* generating XML is not totally consistent, the data carrying classes should have the 'generate' method replaced
* parsing of the incoming JSON-data is currently processed using GSon, probably perfectly ok, but there are som ideas of replacing GSon with an ANTLR generated parser and thus iterating using appropriate visitors for better generation of XML on-the-fly
* more ..?..

## Building
To build this little project Maven is used. 

  To build a library for programmatic use: 
  ```
  mvn clean install
  ```

  To build an executbale JAR:
  ```
  mvn clean compile assembly:single
  ```

## Usage
There are two ways to use this lib, fom the command line or programmatically. 

Running the tool from the commandline: 
```
java -jar json2xml.jar -h
```

Using it programmatically, the entry point is through the `StyleConverter` interface: 

```
  ...
  StyleConverter converter = StyleConverter.getDefaultJsonConverter();
  ThemeFile result = converter.convert(json-string);
  ...
```
