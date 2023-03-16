#!/usr/bin/sh

commit_msg=$(git log -1 --pretty=%B)

prefix=$(echo $commit_msg | head -n 1 | cut -d " " -f1)

echo $commit_msg
echo $prefix

if [[ $prefix =~ /^Release/i ]]
then
  version=$(echo $commit_msg | head -n 1 | cut -d " " -f2)
  echo "Tagging with '$version', message: '$prefix'"

#  git tag -a $version -m "$prefix $version"
fi
