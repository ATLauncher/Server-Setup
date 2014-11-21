ATLauncher Server Setup
====================================

### What is it?

This sets up servers downloaded from ATLauncher.


### Links
[ATLauncher Website](http://www.atlauncher.com)

[ATLauncher Facebook](http://www.facebook.com/ATLauncher)

[ATLauncher Reddit](http://www.reddit.com/r/ATLauncher)

[ATLauncher Twitter](http://twitter.com/ATLauncher)


### Coding Standards

+ Please keep all line lengths to 120 characters and use 4 spaces rather than tab characters
+ Please keep all variables at the top of the class
+ Please keep all inner classes at the bottom
+ Please don't use star imports
+ Please mark all classes that are to be de/serialized with Gson with the @Json annotation for other developers
+ Please use the IntelliJ-Coding-Style.xml for the project (if using IntelliJ) in order to keep all formatting consistent

### Building

#### Requirements

##### Java Development Kit (JDK)

Download and install the latest version from [Oracle's Java Downloads page](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).

##### Apache Maven

Install Apache Maven via the official [Apache Maven Install Docs](http://maven.apache.org/download.cgi#Installation).

##### Fix JavaFX

Open up a terminal to the root directory of the project and run the following command (with admin privelages) 'mvn com.zenjava:javafx-maven-plugin:2.0:fix-classpath' which will make sure JavaFX is in your classpath when compiling.

##### Building a Jar

To build a jar, you need to run 'mvn jfx:jar' which will generate a jar and a lib directory in the target/jfx/ folder


### Versioning System

The versioning system works off the following:

Reserved.Major.Minor.Revision

So for 3.2.1.0 the major number is 2 and minor number is 1 and revision number is 0. Reserved is used as a base, only incremented on complete rewrites.

Major should be incremented when large changes/features are made.

Minor should be incremented when small changes/features are made.

Revision should be incremented when there are no new features and only contains bug fixes for the previous minor.

### Need Help/Have Questions?

If you have questions please don't hesitate to [contact us](http://www.atlauncher.com/contactus/)

### License

This work is licensed under the GNU General Public License v3.0. To view a copy of this license, visit http://www.gnu.org/licenses/gpl-3.0.txt.