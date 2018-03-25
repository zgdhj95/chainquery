ps -ef|grep -v grep|grep $1|while read u p o
do
kill -9 $p
done
