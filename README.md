## 组织结构

* `backend`：服务器后端，SpringBoot+kotlin，按照REST API设计
* `frontend`：网页前端，使用React + ant-design完成

## 如何运行

安装好`docker`和`docker-compose`，在此文件夹运行

```bash
sudo docker-compose up
```

将创建一个数据库（MySQL 8.0.13），一个java程序服务器后端，一个nginx网页服务器（在本机5980端口）。其间将创建一个gradle镜像和一个node镜像分别用于java程序和react app的构建。如欲退出，在此文件夹运行

```bash
sudo docker-compose stop
```

如果想彻底删除所有镜像，从头开始，运行

```bash
sudo docker-compose down
```
