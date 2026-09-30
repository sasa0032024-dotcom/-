# 轻阅 Light Novel Reader

一个纯原生 Java Android 轻量小说阅读器：

- 不申请 INTERNET 权限，不联网、不含广告 SDK。
- 不依赖 AndroidX/第三方运行时库，只有 Android Gradle Plugin 构建依赖。
- 从系统文件选择器导入 TXT，复制到应用私有目录后离线读取。
- 自动识别常见章节标题：第X章/节、序章、楔子、番外、尾声、Chapter X 等。
- 目录点击跳转。
- 字体大小调整。
- 白色 / 米色 / 深色阅读背景。
- 按书保存阅读位置和阅读样式。
- Android 6.0+（minSdk 23）。

## 打包 APK

1. 安装 Android Studio，并让 SDK Manager 安装 Android SDK 35、Build Tools，以及 Android Studio 提示的 JDK（推荐使用 Android Studio 自带 JDK 17）。
2. 用 Android Studio 打开本目录 `LightNovelReader`。
3. 等待 Gradle Sync 完成。
4. 连接 Android 手机并开启 USB 调试，或启动模拟器。
5. 运行调试版：菜单 `Run > Run 'app'`。
6. 生成 APK：菜单 `Build > Build APK(s)`。通常生成在：
   `app/build/outputs/apk/debug/app-debug.apk`
7. 正式版：`Build > Generate Signed Bundle / APK > APK`，创建或选择签名密钥后生成 release APK。

### 命令行

在项目根目录执行：

```bash
./gradlew assembleDebug
```

Windows：

```bat
gradlew.bat assembleDebug
```

输出：
`app/build/outputs/apk/debug/app-debug.apk`

## 设计说明

应用不使用后台服务、不使用网络请求、不使用数据库、不轮询。小说正文复制到应用私有存储，目录和章节索引只在导入/首次打开时计算，阅读过程中主要是一个 TextView + ScrollView，因此资源占用较低。

章节解析采用正则启发式，并不是文学语义分析；格式非常特殊的 TXT 可能需要后续增加规则。
