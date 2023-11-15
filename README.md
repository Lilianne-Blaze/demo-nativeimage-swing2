# demo-nativeimage-swing2

Simple demo for small features making GraalVM native images more Windows-friendly.

Started due to almost complete lack of info about things like that, and my
frustration about GraalVM treating GUI apps as third-class citizens.

Extension of project demo-native-swing1, focusing on embedding dlls that must be
present for Swing/AWT support. No, for some reason they can't be statically linked,
and as far as I know Graal team doesn't see it as a problem.

I assume you already know the basics of GraalVM, and have an environment on which
you can at least compile "hello world" examples.

# Features

Embedding the needed dlls at buildtime, extracting them at runtime to Windows'
temporary directory.

Making necessary adjustments at runtime to prevent renaming the exe from
breaking the app.

# Requirements

Pretty much any distribution of GraalVM for Java 21 should work.

The configuration I'm writing it on is:

Windows 11 Pro,
Netbeans 18,
Visual Studio Community 2022,
Liberica NIK 23.1.0 Full

# Questions

## I don't understand some feature not related to the Swing/AWT dlls?

Most are explained in demo-native-swing1, this projects specifically focuses
on that one feature.

## Why use AWT/Swing when JavaFX is available? Or why mix them?

JavaFX still doesn't have some basic functionality, such as tray icon support.

## Why the exe is so big?

Good question. Note it's 2023 and the main focus is to make "a single exe with
no dependencies", not "a small exe".

Trust me I'd prefer it to be smaller too, but that's not high on my to-do list.

## Will it / can it interfere with or get confused by JREs/JDKs on the target system?

It shouldn't. The whole point is for it to be self-contained as much as possible.

## I tried to run it on some old / unusal configuration and it didn't work?

Please let me know the details. Note that Windows 32-bit or pre-7 will not be supported,
Windows 7 64-bit is the minimum supported configuration.

## This project enlightened me and made me regain my hopes for using Graal for Windows desktop/gui apps, how can I thank you?

Feel free to donate:

https://www.buymeacoffee.com/lblaze

https://paypal.me/lblaze216

## Can I do x? What is the license?

MIT. Basically "do whatever, don't blame me".
