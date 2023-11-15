# Known limitations

## Renaming the exe

Graal creates two special dlls "java.dll" and "jvm.dll" which are linked to the
produced exe, and include that exe's name. Renaming the exe brakes the app as
these dlls can no longer find it.

The workaround is to check if the exe was renamed before AWT is initialized,
and change the name inside these dlls after extracting them. This way the exe
can be renamed, but the new name has to be same or shorter length.

The second part of the workaround is to compile the exe with longer than needed
name, then rename it after compiling.

Look for fragments

```
<imageName>${project.artifactId}-${git.commit.id.full}</imageName>
```

and

```
<move
    file="${project.build.directory}/${project.artifactId}-${git.commit.id.full}.exe"
    tofile="${project.build.directory}/${project.artifactId}.exe"
    verbose="true"/>
```

in pom.xml file.

This gives extra space for 41 chars more.

## Temporary files

The exe needs to extract two sets of dlls, two shims "java.dll" and "jvm.dll",
and a few dlls for AWT.

The second set is extracted once for each JDK on which the app was compiled,
and can be shared between multiple apps.

The first set is always regenerated on compilation, and remains in the temp dir
until it's cleaned up manually or by other software. Note the size is very small,
around 20 kb, so it shouldn't be an issue.

## Compatibility with anti-virus programs

It is possible some AV programs will complain due to extracting and running code
from temporary directory, which is something that few normal apps and lots of
malware do.
At the moment scanning it with VirusTotal gives one false positive.

