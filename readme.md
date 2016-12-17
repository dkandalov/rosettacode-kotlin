
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

### Step 1: Fork, clone and compile

You will need to fork the repository on GitHub and then clone that repository to your working computer – the
usual GitHub workflow. Once you have your local clone, make sure the project compiles 
(it's good to check you can compile the project yourself even if it's green in CI server).

If you want to use [Gradle](https://www.gradle.org), then `./gradlew classes`.

If you want to use [Maven](http://www.maven.org), then `mvn compile`.

### Step 2: Sync this repository with Rosetta Code website

You will need to run a script which checks that Kotlin code in repository 
is still the same as Kotlin code on Rosetta Code website. There are several ways to do it:
 - run `scripts/SyncWithRosettaCode.kt` from within an IDE that understands executing Kotlin programs.
 - in Maven there is current no way to run the script.
 - in Gradle run `./gradlew sync`.  

Possible outputs from the script:
 - all source code files matche perfectly. Move on to the next step :)
 - source code exists on Rosetta Code website but doesn't exist in git repository. The script will automatically download source code. But you will need to compile it, add to git, commit and send a pull request.
 - source code exists in git repository but doesn't exist on Rosetta Code website. There is currently no functionality to upload code to website, so it has to be done manually.
 - source code exists in both git repository and the website, but has different content. In this case you will need to manully find what the difference is, and modify repository or website to keep code in sync.

Files downloaded by the script will have additional `package task_name` line which might not exist on Rosetta Code website. 
This is to avoid name clashes between different tasks. This line won't be considered when diffing repository and website code.
If you use IDE to edit Kotlin code, it might report that package name doesn't match file directory. It's suggested to disable this inspection for this project. 

Note that the script will cache some of the data downloaded from web into `.cache` directory (this is to avoid hitting Rosetta Code on every run). Therefore, you might need to **manually invalidate the cache** by running `rm -rf .cache`.

### Step 3: Add/modify tasks

- Make changes and check that project still compiles (and tests pass).
- Commit, push and send pull request.
- Make your changes on Rosetta Code website.
  This is currently a manual step, i.e. there is no automated way to upload modifications.
  (It might be a good idea to use `<lang scala>` tag because Kotlin doesn't have syntax highlighting on Rosetta Code website at the moment.)
- Rerun `SyncWithRosettaCode.kt` script to make sure repository is still in sync with website. 


### Step 4: Profit

Congratulations! You have just contributed to the World Wide Web!! :octocat:
