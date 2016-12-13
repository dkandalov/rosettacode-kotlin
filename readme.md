## Rosetta Code Kotlin examples

This is a repository with [Kotlin](https://kotlinlang.org/) source code 
from [RosettaCode wiki](http://rosettacode.org/wiki/Category:Kotlin).

The main motivation for extracting all code into a project is to make sure it actually compiles
and make it more maintainable (e.g. by applying static analysis and migrating code as language evolves).

This project is intended to be like a wiki.
Therefore, you are more than welcome to contribute.
All pull requests are accepted.


### How to contribute?

To import example from RosettaCode:
- Clone project and run `mvn compile`.
- Run `scripts/Download.kt` and check that downloaded files are compilable. 
- Commit and send a pull request.

To add new example:
- Add example to this project (it might be a good idea to use `<lang scala>` tag because kotlin doesn't have syntax highlighting at the moment).
- Make sure it compiles and ideally has some tests.
- Commit, send pull request and add example to RosettaCode wiki.


### CI status
[![Build Status](https://travis-ci.org/dkandalov/rosettacode-kotlin.svg?branch=master)](https://travis-ci.org/dkandalov/rosettacode-kotlin)