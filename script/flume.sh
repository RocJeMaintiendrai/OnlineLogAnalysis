#!/bin/bash
#参数:start|stop|restart
#功能:flume 启动停止重启
#使用方法:
#./execflume.sh start flume_cmbc.conf(配置文件,自己修改) Cobub(代理名称,自己修改)
#./execflume.sh stop
#./execflume.sh restart flume_cmbc.conf(配置文件,自己修改) Cobub(代理名称,自己修改)

path=$(cd `dirname $0`; pwd)

echo $path
process=$2
AgentName=$3
JAR="flume"

function start(){

    echo "开始启动 ...."
    num=`ps -ef|grep java|grep $JAR|wc -l`

    echo "进程数:$num"
    if [ "$num" = "0" ] ; then
        #eval nohup java -Xmx512m -jar -DplanNames=$planNames -DconfigPath=$CONFIG_PATH $jarpath/$JAR `echo aliyunzixun@xxx.com|cut -d " " -f3-$#` >> /dev/null 2>&;1 &;
        # 请自行修改启动的所需要的参数
        eval nohup bin/flume-ng agent -c $path/conf -f $path/conf/$process --name $AgentName &;
        echo "启动成功...."
        echo "日志路径: $path/logs/flume.log"
    else
        echo "进程已经存在,启动失败,请检查....."
        exit 0
    fi
}

function stop(){
    echo "开始stop ....."
    num=`ps -ef|grep java|grep $JAR|wc -l`

    if [ "$num" != "0" ] ; then
        #ps -ef|grep java|grep $JAR|awk '{print $2;}'|xargs kill -9
        # 正常停止flume
        ps -ef|grep java|grep $JAR|awk '{print $2;}'|xargs kill
        echo "进程已经关闭..."
    else
        echo "服务未启动,无需停止..."
    fi

}

function restart(){
    echo "begin stop process ..."
    stop

    # 判断程序是否彻底停止
    num=`ps -ef|grep java|grep $JAR|wc -l`

    while [ $num -gt 0 ]; do
    sleep 1

    num=`ps -ef|grep java|grep $JAR|wc -l`
    done

    echo "process stoped,and starting ..."
    start
    echo "started ..."
}

case "$1" in
"start")
    start aliyunzixun@xxx.com
    exit 0
;;
"stop")
    stop
    exit 0
;;
"restart")
    restart aliyunzixun@xxx.com
    exit 0
;;
*)
    echo "用法: $0 {start|stop|restart}"
    exit 1
;;
esac
