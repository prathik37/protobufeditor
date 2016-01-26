Editor for Protocol Buffers Binary Messages (stored in files or called from Java) using a Proto File.

<font size='4'><b>Note:</b> The <b>latest version</b> of the Protocol Buffers Editor is now at:</font>

  * <font size='4'><a href='https://sourceforge.net/projects/protobufeditor/'>https://sourceforge.net/projects/protobufeditor/</a> </font>

```

```


**Functions included**
  * Viewing / Editing a message
  * Editor includes a "proto search" which will try and find the proto-definition for a protocaol buffers data file
  * Comparing Messages

**Limitations currently include**
  * Unknown Fields (i.e. Fields not defined in the Proto File) are not visible.

The project wraps Protocol Buffers objects as RecordEditor objects http://record-editor.sourceforge.net/ then calls the RecordEditor to do the actual editing. It is a member of a family of Editors that use a common core:
  * RecordEditor for Fixed width, csv/tsv, Cobol, Mainframe files
  * CobolEditor Distributed with the RecordEditor for Editing files using a Cobol Copybook or a Xml File description
  * AvroEditor For Avro Binary Files.
  * [reCsvEditor](reCsvEditor.md) for editing basic Csv Files

**Requirements**
  * Java 6 (may work with Java 5 not tested though)
  * Protocol Buffers 2.5.0 Installed (Earlier / later versions may work).
  * Protocol Buffers protoc program must be on the standard path setup.

**Notes**
  * There are some basic notes here http://record-editor.sourceforge.net/ProtoBufIntro.htm


**Sample Screens**

**Message File Open screen**

![http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_open.png](http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_open.png)




**Main Tree Display**

![http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_Editor.png](http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_Editor.png)



**Single Message View**

![http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_Record.png](http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_Record.png)



**Table Display**

![http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_Table.png](http://protobufeditor.googlecode.com/svn/trunk/%20protobufeditor/Documentation/Diagram/ProtoBuf_Table.png)


Version
  * 0.95c Add's support for Protocol Buffers extensions. It is available for download from https://sourceforge.net/projects/protobufeditor/files/ProtoBufEditor/Test_Releases/

  * 0.92 Changes include
    * Change to use Protocol Buffers 2.5.0
    * All screens for a File are now displayed as Tabs rather than as separate screens.
> > > The tabs can be undocked (and redocked) if required.
    * Added Child screen option to List Screens
    * Added Hints dialogue
    * Enhanced HTML Export, option to open exported file with default programs
    * Highlight change fields in compare
    * Minor GUI changes
    * Enhanced Help menu (can open Manual / Forums etc).
    * Problem fixes including Paste Prior issue, 64 Mb exceeded issue (there are
> > > likely to be problems with big files though)


  * 0.88 Changes include
    * New "Proto Search" function. This new option will try and find the proto definition file for "Protocol Buffers Data File".
    * Enhanced Html export + option to Open exported files using the default application
    * Enhanced filter - can use And / Or's + new starts-with operator
    * Find new Starts-With operator
    * Many minor Gui changes + Enhanced Help Menu
    * CSV / Xml Editor can now use a Cobol Copybook to edit a Single record Fixed Width File

  * 0.85 This is a "cleanup" release with only problem fix's and minor changes made.

> > Changes include:
      * Many minor GUI changes mostly related to Windows and Nimbus Laf, but Ctrl-A +
> > > > several other keys are now supported
      * Change Look and feel back to Native on Windows
      * Changes to find function to fix some issues
      * Inclusion of a Csv Editor (essentially ReCsvEditor) in the package. The Csv Editor
> > > > can also view / update existing Xml files.

  * 0.80.6 Based on RecordEditor 0.80.6 and Protocol Buffers 2.4.1
    * Rewritten Save As/Export with a few more options. Changes include:
      * Separate Save As option
      * When selecting a specific export option, only the relavent panel is displayed
      * Can now view / edit Csv, Xml and Fixed export files directly in the Protocol Buffers Editor.
      * Velocity 1.7 is now used and supplied as standard. You can use Velocity to export files.
      * Can export via a Script (Jython, JRuby etc, but you must su7pply the appropriate jar file).
    * New option to control the size of the screen when the editor starts

  * 0.69h Based on latest RecordEditor
    * Fix's problem with display code on insert
    * support for Exporting with Velocity template

  * 0.69g Based on user requests /
    * Support for import in proto's
    * Support for Self Describing messages (2 formats):
      * Delimited File with the first message describing the following message
      * Single field with the first field describing the message.
    * New option to display proto definition of file being edited (View >>>> Show Proto Definition)
    * Can now specify the protoc command + extra options (Edit >>> Edit Startup options >>> Protobuf)

> > Allows the use of non standard protoc or the addition of extra import directories

