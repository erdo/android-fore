#!/bin/sh

#
# this has only been lightly tested on a mac with z shell, it runs a bunch
# of prechecks for fore, before uploading to maven central
#
# only use this if you've read the script
# and you understand what it is doing!
#
# chmod u+x release.sh
# ./release.sh ui|unit|both
#

# exit the script if any statement returns a non-true return value
set -e;
# echo an error message before exiting
trap ' if [ $? != 0 ]; then say -v Moira "hey, something is borked"; fi ' EXIT

TESTS_TO_RUN=$1


print_usage() {
  echo "usage: ./release.sh ui|unit|both"
}

check_parameter_present() {
  if [ -z $TESTS_TO_RUN ]; then
      echo "no parameters specified"
      print_usage ;
      exit 1
  fi
}

check_parameter_valid() {
  if [[ "$TESTS_TO_RUN" != "ui" && "$TESTS_TO_RUN" != "unit" && "$TESTS_TO_RUN" != "both" ]]; then
      echo "unrecognised parameters"
      print_usage ;
      exit 1
  fi
}

check_parameter_present
check_parameter_valid

./gradlew clean

if [[ "$TESTS_TO_RUN" == "unit" || "$TESTS_TO_RUN" == "both" ]]; then
  ./gradlew testDebugUnitTest
fi

if [[ "$TESTS_TO_RUN" == "ui" || "$TESTS_TO_RUN" == "both" ]]; then
  ./gradlew connectedAndroidTest -PtestBuildType=debug --no-daemon --no-parallel
fi

./gradlew publishToMavenLocal

./gradlew publishReleasePublicationToMavenCentralRepository --no-daemon --no-parallel

say -v Moira "Hi, the release has been pushed to maven central"


