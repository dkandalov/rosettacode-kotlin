
[![Build Status](https://travis-ci.org/dkandalov/rosettacode-kotlin.svg?branch=master)](https://travis-ci.org/dkandalov/rosettacode-kotlin)

## Rosetta Code Kotlin examples

This is a repository with the [Kotlin](https://kotlinlang.org/) source code
from [RosettaCode wiki](http://rosettacode.org/wiki/Category:Kotlin).

The main motivation for extracting all the code into a repository is to make sure it actually compiles and
to make it more maintainable (e.g. by applying static analysis and migrating code as the Kotlin language
evolves).

This project is intended to be like a wiki.  You are, therefore, more than welcome to contribute.  All pull
requests will be considered.

## How to contribute?

You will need to fork the repository on GitHub and then clone that repository to your working computer – the
usual GitHub workflow.

Once you have your local clone, you will need to build the download script that refreshes the repository
from Rosetta Code. For the moment it is easiest just to build all the code in the repository (this will
happen eventually so why not do it now).

If you use [Gradle](https://www.gradle.org) then:

- `gradle classes`
- `gradle download`

should do nicely. Or you can execute `scripts/Download.kt` from within an IDE that understands executing
Kotlin programs.

If you use [Maven](http://www.maven.org) then:

- `mvn compile`.
- execute `scripts/Download.kt` – to date the only was of doing this is from within an IDE that understands
executing Kotlin programs.

Then you should check that all the code, old and possibly new, compiles. So either `gradle classes` or `mvn compile`

If there are updated or new classes, you should create a pull request for all successfully compiling
changes. For compilation errors either fix the problem on Rosetta Code or raise an issue on this project.


To add new example yourself:

- Add example to this project (it might be a good idea to use `<lang scala>` tag because kotlin doesn't have
  syntax highlighting at the moment).
- Make sure it compiles and ideally has some tests.
- Commit, send pull request, and add example to the RosettaCode wiki.
