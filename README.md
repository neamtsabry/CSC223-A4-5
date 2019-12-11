# ValleyBikeSim
Smith College / CSC223 / A4/5

## Group Members 

Grace Bratzel, Asmita Gautam, Annika Miller, and Neamat Sabry

## Preconditions:

1. Install the Java JDK (make sure it's version 13); NOT the JRE: 
[https://adoptopenjdk.net/?variant=openjdk13&jvmVariant=hotspot](https://adoptopenjdk.net/?variant=openjdk13&jvmVariant=hotspot) or
[https://www.oracle.com/technetwork/java/javase/downloads/jdk13-downloads-5672538.html](https://www.oracle.com/technetwork/java/javase/downloads/jdk13-downloads-5672538.html)

2. Install Eclipse, (using my link rather than searching for it, since there are many versions):                             [https://www.eclipse.org/downloads/packages/release/2019-06/r/eclipse-ide-java-developers        ](https://www.eclipse.org/downloads/packages/release/2019-06/r/eclipse-ide-java-developers)

## How to import this project into Eclipse

1. Go to the "File" menu and choose "Import"
2. Expand the Git Folder and choose "Projects from Git"
3. Click "Next"
4. Choose "Clone URI"
5. Copy and paste [https://github.com/neamtsabry/CSC223-A4-5](https://github.com/neamtsabry/CSC223-A4-5) into the URI field.
6. Click "Next"
7. Wait for it to find "master"
8. Click "Next"
9. (optional) Choose a directory to store the project.
10. Click "Next"
11. Choose "Import as general project"
12. Click "Next"
13. Click "Finish"
14. Right-Click on the new project that showed up in the "Package Explorer" on the left. Select "Configure > Convert to Maven Project".

## Connect to the database with Eclipse

1. Download the sqlite-jdbc-3.27.2.1.jar file from this link: https://bitbucket.org/xerial/sqlite-jdbc/downloads/
2. Right click on your project
3. Select Build Path
4. Click on Configure Build Path
5. Click on Libraries and select Add External JARs
6. Select the jar file from step 1 from the folder
7. Click and Apply and Ok

## Find and run the code!

1. Expand the "ValleyBikeSim" folder, expand the "src/main/java" and "(default package)" folders.
16. Double-Click on ``ValleyBikeSim.java``
17. Run the code. Do one of:
    - Press the Green Run button at the top.
    - Use the Run menu at the top and select Run.
    - Right-Click and choose "Run As > Java Application"
18. The "Console" tab on the bottom should show you the output of the program. 

## Test the code!

We've made some minor JUnit tests that helped with testing this code. Here's how you can run it:

1. Expand the "ValleyBikeSim" folder, expand the "src/main/java" and "(default package)" folders.
16. Double-Click on ``jUnitTest.java``
17. Run the code. Do one of:
    - Press the Green Run button at the top.
    - Use the Run menu at the top and select Run.
    - Right-Click and choose "Run As > Java Application"
18. The "Console" tab on the bottom should show you the output of the program. 
    
## Instructions for how to run project

### Internal account login info

**username:** alicia <br/>
**password:** csc223 <br/>
**email address:** agrubb@smith.edu 

### Customer account login info

**username:** aliciagrubb <br/>
**password:** csc223 <br/>
**email address:** agrubb@smith.edu

### CITATIONS
https://stackoverflow.com/questions/27005861/calculate-days-between-two-dates-in-java-8 <br/>
https://www.tutorialspoint.com/java/util/uuid_fromstring.htm <br/>
https://www.w3schools.com/sql/sql_delete.asp <br/>
https://www.geeksforgeeks.org/instant-parse-method-in-java-with-examples/

***
