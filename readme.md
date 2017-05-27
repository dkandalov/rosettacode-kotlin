
[![Build Status](https://travis-ci.org/dkandalov/rosettacode-kotlin.svg?branch=master)](https://travis-ci.org/dkandalov/rosettacode-kotlin)
[![Gitter chat](https://badges.gitter.im/rosetta-code-kotlin/chat.svg)](https://gitter.im/rosetta-code-kotlin/Lobby)

## Rosetta Code Kotlin examples

This is a repository with the [Kotlin](https://kotlinlang.org/) source code
from [RosettaCode wiki](http://rosettacode.org/wiki/Category:Kotlin).

The main motivation for extracting all the code into a repository is to make sure it actually compiles and
to make it more maintainable (e.g. by applying static analysis and migrating code as the Kotlin language
evolves).

This project is intended to be like a wiki.  You are, therefore, more than welcome to contribute.  All pull
requests will be considered.


## How to contribute?

In general, the following things can be improved:
 - clean up code (e.g. using IDE suggestions)
 - add unit tests for existing examples
 - update all examples to the latest version of Kotlin


### Step 1: Fork, clone and compile

You will need to fork the repository on GitHub and then clone that repository to your working computer – the
usual GitHub workflow. Once you have your local clone, make sure the project compiles 
(it's good to check you can compile the project yourself even if it's green in CI server).

If you want to use [Gradle](https://www.gradle.org), then `./gradlew classes`.

If you want to use [Maven](http://www.maven.org), then `mvn compile`.

### Step 2: Pull code from Rosetta Code website

You will need to run a script which checks that Kotlin code in repository 
is still the same as Kotlin code on Rosetta Code website. There are several ways to do it:
 - in Gradle run `./gradlew pull`.  
 - in IDE run `scripts/PullFromRosettaCode.kt`.

Note that the script caches data downloaded from web into `.cache` directory.
You can run the script without clearing cache using `./gradlew pullDirty` or `scripts/PullFromRosettaCodeDirty.kt`.
This will make script execution faster but changes from RosettaCode website might be missed.

Possible outputs from the script:
 - all source code files match perfectly. You can move to the next step :)
 - source code exists on Rosetta Code website but doesn't exist in git repository. 
 The script will automatically download source code. But you will need to compile it, add to git, commit and send a pull request.
 - source code exists in git repository but doesn't exist on Rosetta Code website. 
 There is currently no functionality to upload code to website, so it has to be done manually.
 - source code exists in both git repository and the website, but has different content. 
 In this case you will need to manually find what the difference is, and modify repository or website to keep code in sync.
 You can set system property to automatically pull code from website for files with difference, 
 e.g. `./gradlew -DoverwriteLocalFiles=true pull`.

Files downloaded by the script will have additional `package task_name` line which might not exist on Rosetta Code website. 
This is to avoid name clashes between different tasks. This line won't be considered when diffing repository and website code.
If you use IDE to edit Kotlin code, it might report that package name doesn't match file directory. It's suggested to disable this inspection for this project. 


### Step 3: Add/modify tasks

- Make changes and check that project still compiles (and tests pass).
- Commit, push and send pull request on GitHub.
- Update Rosetta Code website:
    - in Gradle run `./gradlew push`.  
    - in IDE run `scripts/PushToRosettaCode.kt`.
  
  You need to have an account on Rosetta Code website to make modifications.    
  Adding new tasks is currently a manual step, i.e. you can only do it from browser.
  (It might be a good idea to use `<lang scala>` tag because Kotlin doesn't have syntax highlighting on Rosetta Code website at the moment.)


### Step 4: Profit

Congratulations! You have just contributed to the World Wide Web!! :octocat:
