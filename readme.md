### Kotlin RosettaCode

This is a repository with [Kotlin](https://kotlinlang.org/) source code 
from [RosettaCode wiki](http://rosettacode.org/wiki/Category:Kotlin).

The main motivation for extracting all code into a project is to make sure it actually compiles
and to make sure it's maintainable (e.g. by applying static analysis and migrating code as language evolves).

This project is intended to be like a wiki with executable code.
Therefore, you are more than welcome to contribute.
All pull requests are accepted.


### How to contribute?
Currently this project includes only a subset of examples from RosettaCode.
The reason is that some of the code snippets require third-party dependencies or don't compile.

To help importing examples from RosettaCode:
- Clone project and in project directory run `mvn compile`.
- Open project in IDE and read main method in `scripts.Download.kt`.
- Modify `Download.kt` so that it includes new examples.
- Run `Download.kt`, check that downloaded files are compilable, add them to git, commit and send a pull request.

To add new example:
- Add example to this project.
- Make sure it compiles and tests pass.
- Add example to RosettaCode wiki.
