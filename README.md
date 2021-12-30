# cement framework

像水泥一样，紧紧的黏固砖块和钢筋


## 主要模块介绍

### cement config
类似于spring cloud config或者nacos config，动态从数据库中读取和刷新配置。
但不同于spring cloud 从git仓库中读取配置，nacos config从独立系统中读取配置，
cement config可以从自定义的SQL中读取配置数据，也就是可以业务系统中动态的读取和业务系统紧密结合的配置。


### cement security(PLAN)

基于Spring Cloud或Shiro等安全框架，实现更贴合实际业务的安全框架，支持用户名密码登录、手机验证码登录、微信小程序登录、第三方应用登录及一键登录等功能。