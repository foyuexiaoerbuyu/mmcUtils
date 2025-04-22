...

https://central.sonatype.com/publishing/deployments
https://blog.csdn.net/q547550831/article/details/137713652
https://blog.csdn.net/agonie201218/article/details/124800163
https://blog.csdn.net/qq_34905631/article/details/136822495
https://central.sonatype.org/register/central-portal/#create-an-account
https://www.cnblogs.com/kenx/p/17538390.html

[如何发布jar包到maven中央仓库（2024年3月最新版保姆级教程）在官方的文档中提到： 所以 2024 年 3 月 - 掘金](https://juejin.cn/post/7347207466818289703)

```
<dependency>
    <groupId>io.github.foyuexiaoerbuyu</groupId>
    <artifactId>mmcUtils</artifactId>
    <version>1.1</version>
</dependency>
```

```
implementation 'io.github.foyuexiaoerbuyu:mmcUtils:1.1'
```

https://central.sonatype.com/artifact/io.github.foyuexiaoerbuyu/mmcUtils

注意要点
1.修改maven的setting文件里给servers添加server节点(这里的信息是中央仓库的id、账号、密码)
2.pom里的gpg节点信息(本地gpg路径和公钥)
3.所有方法必须加注释