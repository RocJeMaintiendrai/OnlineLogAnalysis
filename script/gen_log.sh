#!/bin/bash

function rand(){
  min=$1
  max=$(($2-$min+1))
  num=$(($RANDOM+1000000000)) #增加一个10位的数再求余
  echo $(($num%$max+$min))
}

rnd=$(rand 1 10000)
echo $rnd

while true
do
    rnd=$(rand 1 10000)
    end_pos=`expr $rnd + 50`
    echo $rnd  $end_pos

    sed -n "s/$rnd/$end_pos/p" /home/hadoop/data/test/hadoop-cmf-hdfs-NAMENODE-yws76.log.out.bak
    sed -n "s/$rnd/$end_pos/p" /home/hadoop/data/test/hadoop-cmf-hdfs-NAMENODE-yws76.log.out.bak >>   /home/hadoop/data/test/hadoop-cmf-hdfs-NAMENODE-yws76.log.out

    sleep 60
done
