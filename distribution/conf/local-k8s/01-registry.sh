kubectl create secret docker-registry nacos-registry-secret \
  --docker-server=[加速地址] \
  --docker-username=[阿里云用户] \
  --docker-password=[阿里云镜像仓库密码]\
  --docker-email=[emali] \
  -n nacos
