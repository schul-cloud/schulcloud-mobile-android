Schul-Cloud Android Application
==================

[![Build Status](https://travis-ci.org/schul-cloud/schulcloud-mobile-android.svg?branch=master)](https://travis-ci.org/schul-cloud/schulcloud-mobile-android)

The official Android App for [Schul-Cloud](https://schul-cloud.org/).

**Table of Contents**

- [Setup](#setup)
- [Development](#development)
- [Version History](#version-history)
- [Contribution](#how-to-name-your-branch)
- [Architecture](#architecture)
- [Bug reporting](#bug-reporting)
- [Testing](#testing)
- [Store links](#store-links)
- [Troubleshooting](#troubleshooting)


## Setup

Clone git repository:

```
git clone --recursive https://github.com/schul-cloud/schulcloud-mobile-android
```

## Development

Please use the [Android Studio](https://developer.android.com/sdk/) IDE, since we rely on the Gradle build system.

## Version History

Please push a git tag for every released build.

## How to name your branch

Please feel free to help us with the ongoing development of this app. See open issues to get some inspiration and open a Merge Request as soon as you are ready to go.

1. Take the last part of the url of your Trello ticket or GitHub Issue(e.g. "24-create-setup-instructions-in-readme")
2. Name the branch "trelloid" (e.g. "24-create-setup-instructions-in-readme")

## Architecture

We follow the [MVP pattern](https://antonioleiva.com/mvp-android/) :heart:

![architecture_diagram](https://cloud.githubusercontent.com/assets/9433996/26027704/e473e598-3812-11e7-9545-bbe25ba0caf9.png)

Thanks to [ribot](https://github.com/ribot) for designing the project structure. Read more [here](https://github.com/ribot/android-guidelines/blob/master/architecture_guidelines/android_architecture.md).


## Bug reporting 

We use [fabric.io](https://fabric.io) with [crashlytics](https://fabric.io/kits/android/crashlytics) for repoting bugs. When the App crashes on a device, a Trello-bug-ticket is automatically added. When you still find bugs in the App, please open a new [issue](https://github.com/schul-cloud/schulcloud-mobile-android/issues/new).

## Testing

### Run tests

1. Go into project folder
2. run `./gradlew connectedCheck` for running all tests
3. run `./gradlew check` for also running linting tests

### Create tests

1. Unit Tests goes into the `src/test/` directory, e.g. Model Tests
1. Instrumented Tests goes into the `src/androidTest/` directory, e.g. Activity Tests
1. Common Tests goes into the `src/commonTest/` directory, e.g. Util Tests

## Store links

more to come ..


## Troubleshooting

**Problem**:

```sh
  Error:Execution failed for task ':app:processDebugGoogleServices'.
  > com.google.gson.stream.MalformedJsonException: Use JsonReader.setLenient(true) to accept malformed JSON at line 1 column 17
```

**Solution**:

You need to encrypt the gm-secret file with [git-crypt](https://github.com/AGWA/git-crypt). Ask the Schul-Cloud team for the key-file.
