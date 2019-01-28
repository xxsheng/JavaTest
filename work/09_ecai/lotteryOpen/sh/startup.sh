#!/bin/startup.sh
jarName=LotteryOpen.jar
ps -ef|grep -v grep|grep $jarName|while read u p o
do
 kill -9 $p
done
exec java -jar -Xms512m -Xmx1024m $jarName &