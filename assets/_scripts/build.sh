#!/bin/bash

###############
# CONFIGURATION
###############

# Your AdMob unit id. You need to sign up with AdMob.
#
# See: https://developers.google.com/mobile-ads-sdk/docs/admob/fundamentals#defineadview
# Modifies: activity_node.xml in layout*/
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

# Don't modify this unless you are moving this file.
PROJECT_DIR="."

###########
# FUNCTIONS
###########

function validate_user {
    echo "
    DO NOT PROCEED IF YOU HAVE NOT SET UP THIS SCRIPT'S CONFIG VARIABLES!

    This script is meant to be executed only once, right after a git clone
    (of the master AND submodule repositories).

    It will customize the repository so that the application can compile
    properly, using your own IDs for various components. If you do not set
    up these components (either by running this script or through manual
    configuration of the necessary files), the application will NOT
    run properly. It may not even compile.

    To setup the necessary config variables, open build.sh in a text editor
    and only modify the CONFIGURATION section. Do not modify \$PROJECT_DIR,
    unless you are moving the build.sh file.
    "

  input=""
  echo "Are you ready to run the build? (y/n)"
  read input
  if [ "$input" != "y" ]; then
    message="Build aborted!"
    echo -e "\e[31m$message\e[0m"
    exit 1
  fi
}

function check_sanity {
  isInvalid=false
  if [ -z "$AD_UNIT_ID" ]; then
    isInvalid=true
    message="Error: You must set the AD_UNIT_ID variable"
    echo -e "\e[31m$message\e[0m"
  fi
  if [ -z "$PHYSICAL_DEVICE_ID" ]; then
    isInvalid=true
    message="Error: You must set the PHYSICAL_DEVICE_ID variable"
    echo -e "\e[31m$message\e[0m"
  fi
  if [ -z "$BILLING_PUBLIC_KEY" ]; then
    isInvalid=true
    message="Error: You must set the BILLING_PUBLIC_KEY variable"
    echo -e "\e[31m$message\e[0m"
  fi
  if $isInvalid; then
    message="Build aborted due to errors!"
    echo -e "\e[31m$message\e[0m"
    exit 2
  fi
}

function prepare_layouts {
  target="$PROJECT_DIR/res/layout*/activity_node.xml"
  sed -i "s|YOUR_AD_UNIT_ID|$AD_UNIT_ID|g" $target
}

function prepare_NodeActivity {
  target="$PROJECT_DIR/src/net/globide/creepypasta_files_07/NodeActivity.java"
  sed -i "s|adRequest.addTestDevice(AdRequest.TEST_EMULATOR);|adRequest.addTestDevice(AdRequest.TEST_EMULATOR);\n                    adRequest.addTestDevice(\"$PHYSICAL_DEVICE_ID\");|g" $target
}

function prepare_ShopActivity {
  target="$PROJECT_DIR/src/net/globide/creepypasta_files_07/ShopActivity.java"
  sed -i "s|String base64EncodedPublicKey = getKey();|String base64EncodedPublicKey = \"$BILLING_PUBLIC_KEY\";|g" $target
}

function build_database {
  target_db="$PROJECT_DIR/assets/creepypasta_files.db"
  target_ddl="$PROJECT_DIR/assets/creepypasta-files.sql"
  sqlite3 $target_db < $target_ddl
}

function update_android_project {
  android update project --path $PROJECT_DIR
}

######
# MAIN
######

validate_user
check_sanity
case "$1" in
all)
  prepare_layouts
  prepare_NodeActivity
  prepare_ShopActivity
  build_database
  update_android_project
  message="Build complete!"
  echo -e "\e[32m$message\e[0m"
  ;;
*)
  echo 'Usage:' `basename $0` '[option]'
  echo 'Available options:'
  for option in all
  do 
      echo '  -' $option
  done
  ;;
esac
