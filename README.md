# README #

## RoHAWKtics Scouting Program ##

This is an android application to be used by FIRST Team 3824 for scouting other teams robots during competition in order to come up with the most optimal strategy for any given alliance partners and opponents and for selecting elimination alliance partners.

Version: alpha (pre-numbers)


### How do I get set up? ###
The only two things currently needed to work with this project are a git client ([SourceTree](https://www.sourcetreeapp.com/) is recommended if you are unfamiliar with command line) and [Android Studio](https://developer.android.com/sdk/index.html). With Android Studio code can be tested using either an emulator or an android device connect via usb.

* The repository currently uses two git submodules and I am not perfectly sure that these will clone correctly. If they don't then contact me and we will figure it out. - Andrew

* Also this application will be setup to run on Nexus 7 and Nexus 9 tablets


### What is git? ###

Git is a distributed version control system. Git uses a concept of repositories, branches, and commits to keep track of the history of a collection of files.


### Contribution guidelines ###

At this time, we are planning to use a branch/pull request workflow. There is a nice guide to the flow [here](https://guides.github.com/introduction/flow/).

When implementing a new feature, you should create a branch off of master. Once you have implemented the new feature, you should create a pull request. At this point, the code will enter an informal review process prior to merging into master.

### Issue Tracking/Task board ###
We will be using [Bitbucket Cards](http://www.bitbucketcards.com/amessing/rohawkticsscouting) to keep track of bugs, improvements, and tasks that need to worked on. Any time you find a bug it needs to be added with an adequate description to the cards list, so that we can make sure to fix it. New improvement ideas should also be added, so we can remember them.


###Dependencies###
Currently this project has three dependencies: Volley, Drag and Drop Listview, and MPAndroidChart.

*All should work as submodule, so won't need to be cloned individually. If ever updated testing will need to be done to make sure all uses still work.

####[Volley](https://android.googlesource.com/platform/frameworks/volley)####
Volley is an HTTP library that makes networking for Android apps easier and most importantly, faster. Specifically it is used to pull information from [The Blue Alliance](http://www.thebluealliance.com/) using their [API](http://www.thebluealliance.com/apidocs) such as match schedules and team lists. Guides on using it can be found [here](http://developer.android.com/training/volley/index.html).

*Pulling of information from The Blue Alliance is to be done before the regional/worlds as the devices will need wifi to do it and that may (read probably) not be available at an event.

####[Drag and Drop Listview](https://github.com/JayH5/drag-sort-listview)####
This is currently used in the picklist activity and it allows the creation of a list view that can drag and drop the list items.

####[MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)####
This a charts library which allows the simple creation 8 different types of charts. 
 
### Who do I talk to? ###

This repository is current managed by Andrew Messing (akmessing1@yahoo.com).

### Editing the readme ###
[Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)