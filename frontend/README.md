## 如何运行

在此目录下运行`yarn run start`就会打开浏览器窗口。这是development构建，代码修改会直接热更新体现出来。要求本地机器上有一个运行在`8090`端口的后端服务器，否则前端界面无法正常使用。

## 如何构建

在此目录下运行`yarn run build`就会将前端全部生成在`dist`文件夹内，打包上传服务器即可，和部署静态网站类似。

构建请一定记得修改服务器后端的网址，位于`src/config.ts`的`baseUrl`变量。目前内容为：

```javascript
export const baseUrl = process.env.NODE_ENV === "development" ? "http://localhost:8090/" : "http://120.27.221.101/api/";
```

其意为在development构建模式下，服务器后端为本地机器8090端口的程序；在production构建模式下，服务器后端为`http://120.27.221.101/api/`。

## 文件结构

### `public`

用于存放网页素材，比如图像（jpg、png）、字体（woff）等。

因为所有素材是由webpack负责打包，在引入素材时不要使用类似`<img src="/public/image.jpg">`这种引入方式。要引用图像，参考`src/views/Home/SchemaList.tsx`中导入`briefcase.png`的方式：

```typescript jsx
// import进来叫什么名字不重要，可以随便起，因为名字不是解构对象得到的
import briefcase from "@public/briefcase.png"

const renderItem = (info: SchemaInfo) => <List.Item>
  <List.Item.Meta title={<Link to={`/app/${info.id}/answers`}>{info.name}</Link>}
    {/* 就像这里的`src`，不是写的路径而是`briefcase` */}
    avatar={<Avatar shape="square" size="large" src={briefcase} />}
    description={<span>
      <span className={style.description}>归属中心：{info.creator.group}</span>
      <span className={style.description}>创建时间：{dayjs(info.createdAt).format(datePattern)}</span>
    </span>} />
</List.Item>;
```

`@public`文件夹已经在webpack中配置好路径，就是这个`public`文件夹。

### `src`

存放代码的文件夹，所有的功能性代码都在里面。

* `index.html`：React应用启动需要的html骨架，`<html>`里的内容尽量不要改。
* `index.ts`：React应用的骨架，主要是配置主体的应用路由、redux
* `config.ts`：存放一些全局设置用的变量。我是如此配置的，将来看它不爽可以换成`NODE_ENV`里设置。
* `plugins.ts`：全局生效的框架配置。目前是配置了`dayjs`的locale，里面还有被注释掉的`whyDidYouRender`的配置，需要的时候可以打开。

剩余文件夹作用如下：

* `api`：其内函数封装与服务器后端发起REST请求的动作，以及请求内容与返回内容的类型。在服务器后端修改之后记得在这里对应的修改
* `components`：存放可重用组件，或者某页面专用的小型组件
* `hooks`：存放自定义的hooks
* `redux`：存放`redux`的配置
* `styles`：存放css modules。文件扩展名一定按照`.mod.css`/`.mod.less`来，否则不会生效。有关css modules如何使用，参考`src/views/Login/index.tsx`和`src/styles/login-form.mod.less`。也可以不使用css modules，直接import css/less样式表进来。
* `views`：存放页面组件，因为各自页面上的组件有各自设定，很厚重，且自定义程度太高无法重用，所以这些组件不放进`components`里。一般来说，文件夹里其他文件是该页面专用的组件，由`index.tsx`使用这些组件负责描绘界面。

具体类/函数/组件如何使用，什么作用，参见对应代码注释。

### 配置文件

* `.eslintrc.yml`：yml格式的eslint配置
* `babel.config.js`：babel配置文件
* `module.d.ts`：为了使用css modules和webpack素材而创建的全局类型声明。如果还需要import其他扩展名的素材，按照`"*.png"`去仿写一个即可。不要仿照`"*.css"`和`"*.less"`两个来写，它们是针对css modules而言的
* `postcss.config.js`：postcss配置
* `tsconfig.json`：TypeScript环境配置
* `webpack.base.js`：development和production两种构建模式共用的webpack配置
* `webpack.dev.js`：development模式下专有的配置
* `webpack.pro.js`：production模式下专有的配置

## App入口点

React App打开时默认页面是登录页面，对应页面组件是`src/views/Login/index.tsx`。

登录后或者已经存在有效登录信息时跳转主页面，对应组件是`src/views/App/index.tsx`。但这只是界面骨架，只提供路由、顶栏、侧栏菜单，中间部分真正内容要看`views`文件夹其他的组件。11

## 其他

* 由于使用了`babel-plugin-transform-async-to-promises`，所以所有的async-await都会翻译成Promise而非generator
* 由于开启了`@typescript-eslint/no-floating-promises`，所以意为在后台执行的Promise前面要加个`void`，如`void message.error("出现错误")`
* `redux`是配合`localforage`和`redux-persist`使用的，因此React App重新加载之后`redux`状态也不会丢失
