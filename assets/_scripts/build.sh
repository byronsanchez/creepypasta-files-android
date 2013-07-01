#!/bin/bash

###############
# CONFIGURATION
###############

# Your AdMob unit id. You need to sign up with AdMob.
#
# See: https://developers.google.com/mobile-ads-sdk/docs/admob/fundamentals#defineadview
# Modifies: activity_node.xml in res/layout*/
AD_UNIT_ID=""

# If you are going to install this on a physical device (a real phone as
# opposed to an emulator), you need to place your physical device id here
# so you don't get served real ads.
#
# See: https://developers.google.com/mobile-ads-sdk/docs/admob/best-practices#testmode
# Modifies: NodeActivity.java in src/net/globide/creepypasta_files_07/
PHYSICAL_DEVICE_ID=""

# Your Inapp Billing Public Key. This is NECESSARY if you want the shop
# to work.
#
# See: http://developer.android.com/google/play/billing/billing_integrate.html#billing-security
# Modifies: ShopActivity.java in src/net/globide/creepypasta_files_07/
BILLING_PUBLIC_KEY=""

######################
# SYSTEM CONFIGURATION
######################

# Don't modify this unless you are moving this file.
PROJECT_DIR="."
# Extension sed uses for backups during substitution.
BACKUP_EXT=".bu"

###########
# FUNCTIONS
###########

function validate_user {
    printf "
    DO NOT PROCEED IF YOU HAVE NOT SET UP THIS SCRIPT'S CONFIG VARIABLES!

    This script is meant to be executed only once, right after a git clone
    (of the master AND submodule repositories). Run this script from the
    root of the project directory.

    It will customize the repository so that the application can compile
    properly, using your own IDs for various components. If you do not set
    up these components (either by running this script or through manual
    configuration of the necessary files), the application will NOT
    run properly. It may not even compile.

    To setup the necessary config variables, open build.sh in a text editor
    and only modify the CONFIGURATION section. Do not modify \$PROJECT_DIR,
    unless you are moving the build.sh file.\n\n"

  input=""
  printf "Are you ready to run the build (y/n)? "
  read input
  printf "\n"
  if [ "$input" != "y" ]; then
    message="Build aborted!"
    printf "\e[31m$message\e[0m\n"
    exit 1
  fi
}

function check_sanity {
  isInvalid=false
  if [ -z "$AD_UNIT_ID" ]; then
    isInvalid=true
    message="Error: You must set the \$AD_UNIT_ID variable"
    printf "\e[31m$message\e[0m\n"
  fi
  if [ -z "$PHYSICAL_DEVICE_ID" ]; then
    isInvalid=true
    message="Error: You must set the \$PHYSICAL_DEVICE_ID variable"
    printf "\e[31m$message\e[0m\n"
  fi
  if [ -z "$BILLING_PUBLIC_KEY" ]; then
    isInvalid=true
    message="Error: You must set the \$BILLING_PUBLIC_KEY variable"
    printf "\e[31m$message\e[0m\n"
  fi
  if $isInvalid; then
    message="Build aborted due to errors!"
    printf "\e[31m$message\e[0m\n"
    exit 2
  fi
}

function prepare_layouts {
  target="$PROJECT_DIR/res/layout*/activity_node.xml"
  sub_string="s|YOUR_AD_UNIT_ID|$AD_UNIT_ID|g"
  sed_file "$target" "$sub_string"
}

function prepare_NodeActivity {
  # Have the shell interpret the newline for cross-compatibility.
  # (Some versions of sed won't interpret it).
  lf=$'\n'
  target="$PROJECT_DIR/src/net/globide/creepypasta_files_07/NodeActivity.java"
  sub_string="s|adRequest.addTestDevice(AdRequest.TEST_EMULATOR);|adRequest.addTestDevice(AdRequest.TEST_EMULATOR);\\$lf                    adRequest.addTestDevice(\"$PHYSICAL_DEVICE_ID\");|g"
  sed_file "$target" "$sub_string"
}

function prepare_ShopActivity {
  target="$PROJECT_DIR/src/net/globide/creepypasta_files_07/ShopActivity.java"
  sub_string="s|String base64EncodedPublicKey = getKey();|String base64EncodedPublicKey = \"$BILLING_PUBLIC_KEY\";|g"
  sed_file "$target" "$sub_string"
}

function build_database {
  target_db="$PROJECT_DIR/assets/creepypasta_files.db"
  target_ddl="$PROJECT_DIR/assets/creepypasta-files.sql"
  sqlite3 $target_db < $target_ddl
}

function update_android_project {
  android update project --path $PROJECT_DIR
}

function sed_file {
  file=$1
  sub_string=$2

  sed -i$BACKUP_EXT "$sub_string" $file
  exit_code=$?

  if [ $exit_code != 0 ]; then
    message="Failed to run the build on file \"$file\"."
    printf "\e[31m$message\e[0m\n"
  else
    rm $file$BACKUP_EXT
  fi
}

######
# MAIN
######

case "$1" in
all)
  validate_user
  check_sanity
  prepare_layouts
  prepare_NodeActivity
  prepare_ShopActivity
  build_database
  update_android_project
  message="Build complete!"
  printf "\e[32m$message\e[0m\n"
  ;;
*)
  printf "Usage: `basename $0` [option]\n"
  printf "Available options:\n"
  for option in all
  do 
    printf "  - $option\n"
  done
  ;;
esac
