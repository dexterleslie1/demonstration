#!/bin/sh

# set -x
# set -e

basedir=`pwd`

PATH="/usr/local/bin:/usr/local/sbin:/usr/bin:/bin"

{
LOCK=$basedir/autobackup.lockfile
if [ -f $LOCK ]; then
  echo "Autobackup cronb is running!"
  exit 6
fi
touch $LOCK

prevBaseBackupDir=`date --date '-2 days 3 hour ago' +%Y-%m-%d`
rm -rf $basedir/$prevBaseBackupDir

cd $basedir

currentBaseBackupDir=`date --date '7 hours ago 3 minutes ago' +%Y-%m-%d`
# echo "Current base backup directory:$currentBaseBackupDir"

if [ ! -d "$currentBaseBackupDir" ]; then
 mkdir $currentBaseBackupDir
fi

cd $basedir/$currentBaseBackupDir

echo "Backup directory: `pwd`"

currentBackup=`date +%Y-%m-%d_%H-%M-%S`
# echo $currentBackup

currentBackupFile=$currentBackup.sql
# echo $currentBackupFile
echo "Backup sql file: `pwd`/$currentBackupFile"

mysqldump -u$DBUSER -p$DBPASSWORD -h$DBHOST --single-transaction --quick --lock-tables=false $DBNAME > $currentBackupFile
tar -czf $basedir/$currentBaseBackupDir/$currentBackupFile.tar.gz $currentBackupFile
rm -f $currentBackupFile
aws s3api put-object --bucket $BUCKETNAME --key $BUCKETSUBDIR/$currentBaseBackupDir/$currentBackupFile.tar.gz --body $basedir/$currentBaseBackupDir/$currentBackupFile.tar.gz
}||{
pwd
}

rm $LOCK