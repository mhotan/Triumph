This directory contains native library folders for alljoyn_java and triumph.

In order to use the Native Class loader for an eclipse java project there must
be a nested directory called lib under a folder that has been configured to the build path.

To add a folder to the build path go to
* Project Properties -> Java Build Path
* Select the Libraries Tab
* Add class folder
* Select the folder that contains your lib directory.

Assuming you made a directory called "libs" and added it to your build path as described above
NOTE: directory can be called anything as long as Eclipse knows to add it to your build path.

This is an example of what your directory tree would look like.

-- libs/lib/
---- win-x86/
------ liballjoyn.dll
------ libtriumph.dll
---- linux-x86/
------ liballjoyn.so
------ libtriumph.so
---- linux-x86_64/
------ ...	
---- linux-ia64/
------ ...
