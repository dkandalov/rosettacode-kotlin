
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
usual GitHub workflow.

Once you have your local clone, make sure the project compiles 
(it's good to make sure you can compile the project yourself even if it's green in CI server).

If you want to use [Gradle](https://www.gradle.org), then `./gradlew classes`.

If you want to use [Maven](http://www.maven.org), then `mvn compile`.

### Step 2: Sync local tasks with rosetta code website

You will need to run a script which checks that list of Kotlin tasks in repository 
is the same as the list of Kotlin tasks on Rosetta Code website.
  
There are several to do it:
 - in Gradle run `./gradlew sync`.  
 - in Maven... at the moment there is no way to run it from Maven.
 - run `scripts/SyncWithRosettaCode.kt` from within an IDE that understands executing Kotlin programs.

The script will attempt to download tasks which were added on Rosetta Code website but were not yet added to github.
If there are any differences between repository and Rosetta Code website, they will be reported by script.
Please fix them and send a pull request.

Note that the script will cache some of the data downloaded from web into `.cache` directory.
This was done to avoid hitting Rosetta Code on every run.
Therefore, you might need to invalidate the cache manually by running `rm -rf .cache`.   

### Step 3: Add/modify tasks

- Make changes and check that project still compiles (and tests pass).
- Commit, push and send pull request.
- Make your changes on Rosetta Code website.
  This is currently a manual step, i.e. there is automated way to upload modifications.
  (It might be a good idea to use `<lang scala>` tag because Kotlin doesn't have syntax highlighting at the moment.)
- Rerun `SyncWithRosettaCode.kt` script to make sure repository is still in sync with website. 


### Step 4: Profit

TBD