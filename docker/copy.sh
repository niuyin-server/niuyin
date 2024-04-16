#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
#echo "begin copy sql "
#cp ../sql/ry_20230706.sql ./mysql/db
#cp ../sql/ry_config_20220929.sql ./mysql/db

# copy html
#echo "begin copy html "
#cp -r ../niuyin-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy niuyin-gateway "
cp ../niuyin-gateway/target/niuyin-gateway.jar ./niuyin/gateway/jar

echo "begin copy niuyin-behave "
cp ../niuyin-service/niuyin-behave/target/niuyin-behave.jar ./niuyin/service/behave/jar
echo "begin copy niuyin-creator "
cp ../niuyin-service/niuyin-creator/target/niuyin-creator.jar ./niuyin/service/creator/jar
echo "begin copy niuyin-member "
cp ../niuyin-service/niuyin-member/target/niuyin-member.jar ./niuyin/service/member/jar
echo "begin copy niuyin-notice "
cp ../niuyin-service/niuyin-notice/target/niuyin-notice.jar ./niuyin/service/notice/jar
echo "begin copy niuyin-search "
cp ../niuyin-service/niuyin-search/target/niuyin-search.jar ./niuyin/service/search/jar
echo "begin copy niuyin-social "
cp ../niuyin-service/niuyin-social/target/niuyin-social.jar ./niuyin/service/social/jar
echo "begin copy niuyin-video "
cp ../niuyin-service/niuyin-video/target/niuyin-video.jar ./niuyin/service/video/jar


