# What's where

InlineAwtLoader - main entry point, you need to call its "extractAndLoad" method
before loading anything related to Swing/AWT, i.e. preferably the first thing
your app does, or just after initializing logging. For now consider it the only
"public API".

ExecutableNameFinder - utility class to find the exe's name during compilation,
meant to be used from custom Graal Features.

LogFeature - simple Feature just to make you see which method is called where
and in what order.

ResourceBTUtils - utils for injecting arbitrary files or byte arrays into exe.

ContainerBTUtils - utils for creating "containers" - "holes" in the exe which
can be filled as needed after compilation. Needed for embedding "java.dll" and
"jvm.dll" which are remade during each compilation at the same time the exe is,
so they can't be injected using normal means.

InlineAwtFeature, InlineAwtPreparator - main logic.

