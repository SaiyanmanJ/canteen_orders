从零实现一个校园点餐系统(可能只是部分功能)。

作为微服务练手项目,不会追求业务的完全,可能只会完成部分功能，然后继续深入扩展，而不是横向扩展业务。

开发期间会在语雀上记录完整历程，完成后公开记录。这里只记录功能的实现。

目前使用框架，组件：
* SpringBoot
* mybatis
* SpringCloud Alibaba
  * nacos
* SpringCloud
  * OpenFeign
  * Stream
  * Gateway
  * Zipkin
  * Sleuth
* mysql
* redis
* rabbitmq
* docker

测试用工具：
* postman 测请求
* jemeter 压测
2021/10/11
* 创建项目

2021/10/13
* 商品服务：普通crud, 减库存
* 订单服务：下订单

2021/10/14
* 整合了nacos作为配置中心

2021/10/15
* 数据源的动态刷新

2021/10/16
* 加入rabbitmq，商品服务扣减库存后，通过mq通知订单服务的消息队列

2021/10/17
* 加入Spring Cloud Gateway, 可以根据配置中心的配置动态更新自定义路由信息

2021/10/18
* 创建用户服务，实现用户登录功能
* Spring Cloud Gateway 鉴权，创建订单只能由买家请求，设置订单完成状态只能由卖家请求
* 加入 zipkin sleuth 进行服务跟踪


