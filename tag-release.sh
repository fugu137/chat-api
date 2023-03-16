#!/usr/bin/sh

commit_msg=$(git log -1 --pretty=%B)

if [[ ${commit_msg^^} =~ ^RELEASE ]]
then
  version=$(echo $commit_msg | head -n 1 | cut -d " " -f2)

  if [[ $version =~ ^v[0-9].[0-9].[0-9] ]]
  then
    echo "match"
  fi
  echo "Tagging last commit with tag: '$version'"
#  git tag -a $version -m ""
fi
