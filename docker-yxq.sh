BUILD_NUMBER=$1
APT_NAME="yxq"
API_VERSION="1.0"
API_PORT=9999
IMAGE_NAME="$API_NAME:$BUILD_NUMBER"
CONTAINER_NAME=$API_NAME-$APT_VERSION
docker --version
cd /root/swsad
docker build -t yxq:zgl .
cid=$(docker ps -a| grep "yxq-1.0" | awk '{print $1}')
if [ "$cid" != "" ]; then
	docker rm -f $cid
fi
docker run -d --net=host -p 9999:9999 --privileged=true -v /root/swsad/images:/images --name yxq-1.0 yxq:zgl
