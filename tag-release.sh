#!/usr/bin/sh

commit_msg=$(git log -1 --pretty=%B)

if [[ ${commit_msg^^} =~ ^RELEASE ]]
then
  version=$(echo $commit_msg | head -n 1 | cut -d " " -f2)

  if [[ $version =~ ^v[0-9].[0-9].[0-9](-M[0-9]+)?$ ]]
  then
    echo "Tagging last commit with tag: '$version'"
    git tag -a $version -m ""
  else
    echo "Invalid version '$version'. Should have the form vx.x.x[-<optional modifier>]. Aborting..."
    exit 1
  fi

fi
