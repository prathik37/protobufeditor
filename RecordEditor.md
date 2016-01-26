# Introduction #

The RecordEditor is Data Oriented editor. Its main focus is Fixed Width (Text Binary), CSV files and to a lessor extent XML files.

But it can be called with any data that implements the appropriate Record-Editor Interfaces.


# Details #

The RecordEditor can edit
  * Fixed Width Text / Binary files
  * CSV file
  * Xml files (fairly limited)
  * Cobol (Mainframe/Fujitsu/Open Cobol)

As well as editing files it also
  * compares files
  * Copies files from one format to another (i.e. Cobol to/from Csv)

The project is hosted at sourceforge http://record-editor.sourceforge.net/

# RecordEditor Family #

The core part of the **RecordEditor** operates on several Interfaces which include

  * **AbstractLine**  -  Generic Record or Line from a File
  * **AbstractLayoutDetail** - Generic schema definition

The Editor core will work with any classes that implement all the appropriate
interfaces. Currently there are4 front ends

  1. **RecordEditor** - For editing Fixed Width, csv/tsv delimited, Cobol, Mainframe and Xml (needs so work though) Files.
  1. **CobolEditor** - comes as part of the RecordEditor package. It can edit Files using either a Cobol or Xml Copybook (or file Description.
  1. **protobufeditor** - For Editing Protocol Buffer binary files
  1. **AvroEditor** - For editing Avro Binary serialised files. It is available at http://avroeditor.sourceforge.net
  1. **[reCsvEditor](reCsvEditor.md)** - For editing basic Csv Files. It is available at http://recsveditor.sourceforge.net/