## 如何构建

在此文件夹下运行`./gradlew build --console=rich`即可，会输出构建过程的详细信息和进度条。构建结果在`build/libs/backend-0.0.1-SNAPSHOT.jar`。

## 如何运行

使用`java -jar <path-to-jar>`即可，但还有一些参数需要调整。

运行时需要给出MySQL数据库（开发本后端系统时的版本为8.0.13）的端口号，默认是`8090`，修改时添加参数`-Dserver.port=<port>`。数据库的启动和配置可以借助我提供的docker-compose。

如果是部署在服务器上，需要关闭debug模式（默认为开启），添加参数`-Ddebug=false`。

举例：数据库端口在`8080`，关闭debug模式

```bash
java -jar backend.jar -Dserver.port=8080 -Ddebug=false
```

默认参数的配置在`src/main/resources/application.yml`内。

## 文件结构

### 系统代码`src/main/kotlin`

#### `common`

存放的内容是一些常用函数和工具类，起辅助作用。

#### `component`

存放一些Bean，由于并不属于`@Service`、`@Controller`、`@Repository`的任何一个，但又需要用到Spring的Bean机制，因此用`@Component`标记，放在这里。

#### `config`

SpringBoot的配置

#### `controller`

Controller层，为REST API对外的接口，是Service层的简单包装

#### `repository`

数据持久化层，使用Spring Data JPA机制，和Hibernate的JPA规范定义的数据库实体类。

为了代码的简单，使用了JPA的[Projection](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections)特性，和JPA的[Specification](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications)特性。

Projection能让Hibernate只返回数据库的某几列而非将整个对象序列化了返回回来，但它的支持有些问题，因此使用了th.co.geniustree.springdata.jpa:specification-with-projections类库来实现Projection特性。

Specification能使用代码直接自定义查询，不需要单独在Repository接口里写sql语句。在本项目里并未直接使用，而是使用了au.com.console:kotlin-jpa-specification-dsl类库，借助kotlin的语法特性简化了Specification的用法。

复杂查询则是使用了JPA的`@Query`，在Repository里写出接口，提供JPQL（JPA规范定义的JPA专用的类SQL语言），然后依靠框架自动生成查询实现。

#### `service`

在Repository层上提供复杂功能，以供Controller使用。

### 测试代码`src/test/kotlin`

测试API接口的代码

## 其他

* 使用kotlin是为了它的null-safety特性以及其他的方便功能。
* 因为kotlin的`$`字符串具有特殊含义，因此使用`@Value`引用配置参数的时候，`$`需要转义写成`\$`。
* 所有Bean的注入`@Autowired`除非特殊情况，使用constructor注入。且constructor注入时，注入依赖可以不写`@Autowired`。所以`@Service`、`@Controller`等的注入全部使用kotlin的primary constructor语法写在构造函数里，且没有`@Autowired`。
* 有些信息比较复杂的实体类，分为`Entity`和`EntityInfo`两种返回数据，前者是所有的数据，后者是简略的数据。因为有些功能并不一定点进去看详情，而且完整获取一个实体开销较大，所以分开设计成两个。
* `@Entity`的实体设计，关于外键的配置，在逐步转向Hibernate提供的`@XXToYY`风格的映射。
* 在设计Controller的接口时，如果能到达Controller层则说明已经通过`FilterChain`的登录验证，携带的是合法的token，因此后续如果需要当前登录用户信息直接使用`Authentication`或者`Principle`的用户信息即可。
* REST API和Json Web Token的风格都是无状态的，尽量保持这个风格。
* `ResponseStatusException`在Controller层抛出会自动转换成错误信息处理以及HTTP错误码，因此只能在Controller层的方法内部抛出这个异常。
* （**注意**）`src/main/resources/application.yml`的`jwt.signing-key`是token加密使用的密钥。**部署服务时一定记得重新生成一个！**
