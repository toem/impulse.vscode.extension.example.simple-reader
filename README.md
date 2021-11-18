



# Simple Reader template for impulse.vscode

This template enables the creation of a custom VsCode/Theia plugin for handling signal data (waveforms, logs, traces, simulation results, ...) from files, streams or other resources. 
The code of this template is provided to you under the terms and conditions of the MIT License.

A reader parses the data of a given format and converts its contents into the internal representation. This article shows how to create a simple or stream reader creating custom signals.
In contrast to a normal reader, a stream reader allows to load partial streams. Imagine a simulation process that writes its results into a pipe. With a stream reader, impulse will show all signals that were written until a certain point of time.


## Use gitpod

[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/toem/impulse.vscode.extension.example.simple-reader)

Just enter [https://www.gitpod.io#https://github.com/toem/impulse.impulse.vscode.extension.example.simple-reader](https://www.gitpod.io#https://github.com/toem/impulse.impulse.vscode.extension.example.simple-reader) into your browser and log-in with your github account.

## or pull into you local vscode 

* Download the template and open vscode
* Install the following plugins:
    -  toem-de.impulse
    -  redhat.java
    -  vscjava.vscode-java-debug

## Step1 : npm install

* Open a terminal in vscode and do a friendly

        npm install

* This will install all required node libs.   


## Step2 : Activate Java

* Open the 'SimpleReader.java' file in bundles/src/....  .This will activate the Java subsystem.   
* At the beginning you will see a bunch of red colored errors in the code.
* The java subsystem tries compiles in background.  
* If the errors don't vanish after a short period, goto Step 3 , otherwise step over to 4.

## Step 3 : Correct the library pathes (if errors)

* The java bundle of this template needs the libraries of impulse. They can be found in the impulse vscode plugin in the 'impulse/' and impulse/plugins/' folder.
* The location of the vscode plugins is system dependend, so we just add all positibilites in the 'settings.json' file.
* Open the '.vscode/settings.json'

        "java.project.referencedLibraries": [
            
            // standard installation
            "~/.vscode/extensions/toem-de.impulse-*/impulse/*.jar",
            "~/.vscode/extensions/toem-de.impulse-*/impulse/plugins/*.jar",

            // gitpod installation
            "/home/gitpod/.vscode-remote/extensions/toem-de.impulse-*/impulse/*.jar",
            "/home/gitpod/.vscode-remote/extensions/toem-de.impulse-*/impulse/plugins/*.jar"
        ],

* Check if the installation of the impulse plugin is in one of the existing references - if not you need to add.
* After saving the 'settings.json', the java subsystem will try to re-compile.

## Step 4 : Do a first start

* Press F5
* In the newly opened vscode child session:
    * File>Open Folder and select the folder bundle/samples of the template.
    * Select'impulse.reader.example' and Open With...  impulse.
    * In the upper left field, select the 'Simple Reader' in 'Extension Examples'
    * Create a new view.
    * You should see this:
 


* Stop the session.

## Step 5 : Do you want a stream reader ?

* A stream reader allows to read partial streams. If you need to parse the whole file/content to get to valid output, the answer is NO.
* Open the 'SimpleReader.java' file in bundles/src/.... and search for this code below.
* In case of a stream reader return 'true, otherwise 'false'

        // ========================================================================================================================
        // Stream Reader
        // ========================================================================================================================

        @Override
        public boolean supportsStreaming() {
            return true;
        }


## Step 6 : Choose the input stream handling

* The input for the parser is a java input stream. 
* In case of a text format, you might use a 'new BufferedReader(new InputStreamReader(in))'.
* Below you find the code for typical text and binary input stream handling. You find both in the 'SimpleReader.java'. But you may do it differently.
* The binary version calls the the function parse with a block of data, the text version calls parse with a line content.

        // text reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        int linesProcessed = 0;

        // text line loop
        while ((line = reader.readLine()) != null && (progress == null || !progress.isCanceled())) {
            parse(line);
            linesProcessed++;
        }

        // or binary reader
        byte[] buffer = new byte[4096];
        int filled = 0;
        int read = 0;


        // binary data loop
        while((read = in.read(buffer, filled, buffer.length-filled))>=0 && && (progress == null || !progress.isCanceled())){
            filled+=read;
            int used = parse(buffer,filled);
            System.arraycopy(buffer, used, buffer, 0, filled-used);
            filled -= used;
        }


## Step 7 : Implement the parse line/block function

* Implement the parser, either for the text line or a binary block of data.
* In case of binary data, return how much data of the block was used.
* This step does not include the creation of signals.
* The java subsystem will compile the code in background and will show possible problems and errors.
* Throw a ParseException in case of parsing error.
* Use the 'Utils.log("parses a line",23,34)' function to write logs to the output.

## Step 8 : Enable java debugging

* You need to add parameters to the java engine of impulse.
* Go to settings and select Extensions/impulse.
* Add the following to the 'Java' parameter (starting from -agentlib...): 

        java -Xms128m -Xmx2048m -agentlib:jdwp=transport=dt_socket,address=127.0.0.1:8888,server=y,suspend=n



## Step 9 : Debug the parser

* Next step is to start again and attach the java debugger.
* Do 'Step4' again with your custom file.
* Under 'Run and Debug'  select 'Debug (Attach)' and start (in the main session - not the child session).
* You can set a breakpoint in the parse function of your reader.
* Press the 'Re-load' toolbar button in impulse (child session). The reader is called and should enter the main parse function.


## Step 10 : Init the reaord, set the domain base

* If the reading of the file works properly , you can start to add signal content (scopes and signals).
* Use this manual pages for introduction.
    * [19 Signal Content](https://toem.de/index.php/resources/all-documents/87-19-signal-content)
* Find the code snippet below in the example reader and modify the record name and domain base.    

        // ========================================================================================================================
        // Create record
        // ========================================================================================================================

        initRecord("Example File", TimeBase.ms);


## Step 11 : Add signals

* The example code adds signals in the 'addExampleSignals()' function. Uncomment both 'addExampleSignals()' and 'writeExampleSamples()' function, when adding own signals.
* You may create signals before parsing, like in the example, or while parsing.
* Whenever signals or scopes are added, update the change flag 'changed = CHANGED_RECORD;'
* Use this manual pages to understand signal.
    * [J001 Creating records](https://toem.de/index.php/resources/all-documents/77-j01-creating-records)
    * [Documentation](https://toem.de/index.php/resources/documentation) - scroll down to impulse jdk


## Step 12 : Write samples

* The example code adds signals in the 'writeExampleSamples()' function. Uncomment both 'addExampleSignals()' and 'writeExampleSamples()' function, when adding own signals.
* Whenever samples are written, update the change flag 'changed = changed > CHANGED_SIGNALS ? changed : CHANGED_SIGNALS;'
* Use this manual pages to understand signal.
    * [J001 Creating records](https://toem.de/index.php/resources/all-documents/77-j01-creating-records)
* If your changes are working continue with 13


## Step 13 : Modify the reader IDs

* Open the plugin.xml under bundels
* Modify the id of the serializer 'id="de.toem.impulse.serializer.examples.simple-reader"'  (e.g to my.serializer)
* Modify the id of the contentType 'id="de.toem.impulse.contentType.example"' and the relation in the serializer 'contentType="de.toem.impulse.contentType.example"' (e.g to my.contentType)
* Optionally change the class name both in the java file and in the plugin.xml

        <?xml version="1.0" encoding="UTF-8"?>
        <?eclipse version="3.4"?>
        <plugin>
        <extension
                point="de.toem.toolkits.pattern.serializer">
            <serializer
                    contentType="de.toem.impulse.contentType.example"
                    defaultConfiguration="true"
                    description="%serializer.description"
                    enabled="true"
                    group="%serializer.group"
                    id="de.toem.impulse.serializer.examples.simple-reader"
                    label="%serializer.label"
                    reader="de.toem.impulse.extension.examples.reader.SimpleReader">
            </serializer>
        </extension>
        <extension
                point="org.eclipse.core.contenttype.contentTypes">
            <content-type
                    base-type="de.toem.impulse.contentType.record"
                    file-extensions="example"
                    id="de.toem.impulse.contentType.example"
                    name="%content-type.name"
                    priority="normal">
            </content-type>
        </extension>
        <extension
            point="de.toem.toolkits.pattern.group">
            <group
                description="%group.description"
                icon="icons/extension.png"
                id="de.toem.impulse.group.examples"
                label="%group.label">
            </group>
        </extension>
        </plugin>


## Step 13 : Modify the extension manifest 'package.json'

* Every Visual Studio Code extension needs a manifest file: [Extension Manifest](https://code.visualstudio.com/api/references/extension-manifest)
* Edit the 'package.json' file, especially modify these values:

        "name": "my.simple-reader",
        "displayName": "mysimple-reader",
        "description": "",
        "publisher": "myself",
        "homepage": "https://www.myside.de",
        "author": {
            "name": "ItsMe"
        },
        "license": "MIT",
        "version": "0.1.0",
        "icon": "bundle/icons/extension.png",

## Step 14 : Create the extension vsx package file

* Do a:

        vcse package


## Step 15 : Publish

* (VsCode publishing)[https://code.visualstudio.com/api/working-with-extensions/publishing-extension]
* (OpenVsx publishing)[https://github.com/eclipse/openvsx/wiki]