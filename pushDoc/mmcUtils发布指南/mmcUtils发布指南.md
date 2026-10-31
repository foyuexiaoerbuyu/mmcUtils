---
AIGC:
    Label: "1"
    ContentProducer: 001191440300708461136T1XGW3
    ProduceID: 58e87cd5ac29bab7322874bd6b207f1c_cc908a2cd34611f18068525400de85a5
    ReservedCode1: 2r6WLSDdni6YQDAaXDRfxleUlA/XCpJN+S6Fhcea6aNV5pnQm8Xm0J+4HTqHUs+yTQBh6RaX47eU5e51QjXGJ4iv8pe9iB696yscZR9tmDl7jGxkkYWUPb082nDI31E3LPu8v6IrZNXTodSY1ZhILjWFYdiiU+/SCBi7spncISB5anemhZYcXGI+rHM=
    ContentPropagator: 001191440300708461136T1XGW3
    PropagateID: 58e87cd5ac29bab7322874bd6b207f1c_cc908a2cd34611f18068525400de85a5
    ReservedCode2: 2r6WLSDdni6YQDAaXDRfxleUlA/XCpJN+S6Fhcea6aNV5pnQm8Xm0J+4HTqHUs+yTQBh6RaX47eU5e51QjXGJ4iv8pe9iB696yscZR9tmDl7jGxkkYWUPb082nDI31E3LPu8v6IrZNXTodSY1ZhILjWFYdiiU+/SCBi7spncISB5anemhZYcXGI+rHM=
---

# mmcUtils 发布指南（换机可照做）

> 本文件用于在更换电脑后，根据本指南从零恢复发布环境并发布 mmcUtils 新版本到 Maven Central 与 GitHub。

## 一、项目与发布关键信息

| 项目                | 值                                           |
| ----------------- | ------------------------------------------- |
| 项目路径              | D:\02Code\09github_me\mmcUtils              |
| groupId           | io.github.foyuexiaoerbuyu                   |
| artifactId        | mmcUtils                                    |
| GitHub 仓库         | https://github.com/foyuexiaoerbuyu/mmcUtils |
| GPG 私钥备份          | pushDoc\mmcUtils_gpg_private.asc            |
| GPG 公钥备份          | pushDoc\mmcUtils_gpg_public.asc             |
| Maven settings 备份 | pushDoc\mmcUtils_settings_backup.xml        |
| GPG 程序路径          | C:\Program Files (x86)\GnuPG\bin\gpg.exe    |

## 二、GPG 密钥信息

- 主密钥指纹：`76DAC59E934821F7400C6C545BA49797266A5CDA`
- UID：`mmc <1950053842@qq.com>`
- 签名子密钥：ed25519（指纹 `FD739AACD820094BD89F55358390EFFAB6A63E0C`）
- 私钥口令（passphrase）：`1950053842@qq.com`

## 三、换机后恢复 GPG 密钥

1. 安装 GnuPG（本机版本 2.4.7，Gpg4win 安装包或直接拷贝 gpg.exe）。
2. 导入私钥备份：

```powershell
gpg --import "D:\02Code\09github_me\mmcUtils\pushDoc\mmcUtils_gpg_private.asc"
```

3. 确认密钥存在：

```powershell
gpg --list-secret-keys --keyid-format long 76DAC59E934821F7400C6C545BA49797266A5CDA
```

4. 如 keyserver 上公钥缺失，重新上传公钥（Central 校验主要查 keys.openpgp.org）：

```powershell
gpg --keyserver keyserver.ubuntu.com --send-keys 76DAC59E934821F7400C6C545BA49797266A5CDA
gpg --keyserver pgp.mit.edu --send-keys 76DAC59E934821F7400C6C545BA49797266A5CDA
gpg --keyserver keys.openpgp.org --send-keys 76DAC59E934821F7400C6C545BA49797266A5CDA
```

## 四、恢复 Maven settings.xml

将 `pushDoc\mmcUtils_settings_backup.xml` 复制到 `C:\Users\<用户名>\.m2\settings.xml`。

内容要点（含 Sonatype token 与 gpg 口令，切勿提交到 git）：

```xml
<settings>
  <servers>
    <server>
      <id>mmcUtils</id>
      <username>CUOBNsAo</username>
      <password>jcX9GVNO8plmMihgeKKABuzMnmWzoVL6S5nZCqppYTJS</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>central-publish</id>
      <properties>
        <gpg.passphrase>1950053842@qq.com</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>central-publish</activeProfile>
  </activeProfiles>
</settings>
```

## 五、pom.xml 发布配置（已在项目中，无需改动）

| 插件                              | 版本     | 关键配置                                                                                    |
| ------------------------------- | ------ | --------------------------------------------------------------------------------------- |
| central-publishing-maven-plugin | 0.12.0 | publishingServerId=mmcUtils、autoPublish=true、tokenAuth=true                             |
| maven-gpg-plugin                | 3.2.7  | executable 指向本机 gpg.exe、keyname=密钥指纹、passphrase=${gpg.passphrase}、pinentryMode=loopback |
| maven-javadoc-plugin            | 2.9.1  | additionalparam=-Xdoclint:none（关闭 javadoc 严格校验）                                         |
| maven-source-plugin             | 2.2.1  | 生成 sources jar                                                                          |

## 六、发布新版本步骤

1. 修改版本号：编辑 `pom.xml` 中 `<version>1.4</version>` 为要发布的版本（如 1.5）。
2. 执行构建与发布（已跳过测试）：

```powershell
cd D:\02Code\09github_me\mmcUtils
mvn clean deploy
```

3. 发布成功后提交代码并打 tag：

```powershell
git add -A
git commit -m "v1.5"
git tag v1.5
git push origin master --tags
```

4. 到 https://central.sonatype.com 登录查看发布状态（autoPublish=true 会自动发布）。

## 七、常见问题排查

- **Unusable secret key**：密钥缺少 sign 能力子密钥。本备份密钥已含 ed25519 签名子密钥；若重新生成密钥，需 `gpg --quick-add-key <主指纹> ed25519 sign 2y` 补签名子密钥。
- **GPG 签名超时 / 弹窗卡住**：确认 pom 中 pinentryMode=loopback，且 settings.xml 有 gpg.passphrase 属性。
- **Central 报公钥找不到**：确认公钥已上传 keys.openpgp.org（见第三节第 4 步）。
- **javadoc 打包报错**：确认 maven-javadoc-plugin 配置了 `-Xdoclint:none`。

## 八、安全提醒

- `pushDoc/*.asc` 与 `pushDoc/*.xml` 已在 .gitignore 中，私钥与 token 严禁提交到 GitHub。

- 私钥备份文件请妥善保管（可另存到移动硬盘/密码管理器）。
  *（内容由AI生成，仅供参考）

- 坐标

  ```xml
      <dependency>
              <groupId>io.github.foyuexiaoerbuyu</groupId>
              <artifactId>mmcUtils</artifactId>
              <version>1.4</version>
          </dependency>
  ```
