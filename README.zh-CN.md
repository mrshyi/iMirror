<div align="center">

<img src="docs/hero.png" alt="iMirror — 适用于 Android TV 的免费 AirPlay 接收器，可镜像 iPhone、iPad 和 MacBook 屏幕" width="100%">

# iMirror — Android TV 的 AirPlay 屏幕镜像接收器

[English](README.md) | **简体中文**

**将 iPhone、iPad 或 MacBook 屏幕镜像到 Android TV。** 无需 Apple TV，没有广告、
水印或订阅。

[![许可证：GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![平台](https://img.shields.io/badge/platform-Android%20TV%20%7C%20Google%20TV%20%7C%20Fire%20TV-3ddc84)](#支持的设备)
[![Android](https://img.shields.io/badge/Android-7.1%2B%20(API%2025)-3ddc84)](#支持的设备)
[![下载 APK](https://img.shields.io/badge/download-APK-5ac8fa)](https://github.com/mrshyi/iMirror/releases)
[![永久免费](https://img.shields.io/badge/price-free%20forever-brightgreen)](#无广告无订阅没有套路)

</div>

---

## 为什么开发 iMirror

**许多 Android TV 和智能电视没有内置 AirPlay。** 不同品牌可能将内置功能称为
*AirPlay*、*AirScreen*、*Air Mirror*、*屏幕镜像*、*Smart View* 或 *Miracast*；不少
入门级或地区限定型号则完全没有这项功能、功能无法正常使用，或只能通过充满广告的付费应用实现。

iMirror 可以解决这个问题。它是一个安装在 **Android TV 上的 AirPlay 接收器 APK**，
让电视显示在 iPhone、iPad 或 Mac 的原生“屏幕镜像”列表中。使用方式与连接 Apple TV
类似：手机或电脑无需安装额外应用，也不需要账号或线缆。

## 功能

| 功能 | 说明 |
|---|---|
| 📱 **iPhone 和 iPad 屏幕镜像** | 原生显示在 iOS 控制中心 →“屏幕镜像”中 |
| 💻 **MacBook 屏幕镜像** | 使用相同的 AirPlay 协议，可从 macOS 控制中心连接 |
| 🔊 **音频** | 支持 AAC-ELD、AAC-LC 和 ALAC；音视频可独立开始和停止 |
| ▶️ **视频串流** | 支持 YouTube、照片等可发送串流 URL 的应用，可播放、暂停和拖动进度 |
| 🖼️ **照片** | 全屏显示从 iOS“照片”应用发送的图片 |
| ⚡ **低延迟** | 使用硬件 H.264 解码并直接输出到显示表面，适合交互使用 |
| 🔒 **可选 PIN 配对** | 默认关闭；启用后可阻止附近其他人连接 |
| 🎮 **电视遥控器控制** | 支持 DACP 反向遥控，可用电视遥控器控制手机上的播放 |

## 无广告、无订阅、没有套路

没有付费版、没有水印、没有分辨率限制，也不会弹出“升级到 1080p”的提示。应用**不含
任何账单、广告、跟踪或分析代码**，也不会请求位置权限。项目采用 GPL-3.0 开源许可，
欢迎阅读代码自行核验。

## 下载

可安装 APK 发布在 [GitHub Releases 页面](https://github.com/mrshyi/iMirror/releases)。
每个版本提供各支持 CPU 架构的独立 APK；如果不确定设备架构，也可以下载通用版 APK。

| 设备 | 下载版本 |
|---|---|
| 大多数 Android TV 盒子、电视棒和 Fire TV | `armeabi-v7a` APK |
| 较新的 4K Android TV / Google TV | `arm64-v8a` APK |
| Intel 设备、ChromeOS、模拟器 | `x86` 或 `x86_64` APK |
| **不确定设备架构** | **Universal APK**，包含所有支持的 ABI |

如果不知道电视使用哪种 CPU，可选择通用版 APK。具体文件名和安装注意事项请查看各版本说明。
预发布 APK 使用专用的新签名证书；如果 Android 提示签名不匹配，请先卸载旧版本再安装。

## 在 Android TV 上安装

在电视上启用**开发者选项 → USB 调试**，然后在电脑上执行：

```sh
adb connect <电视的 IP 地址>
adb install -r iMirror-1.0.1-rc.1-universal-release.apk
```

如果没有电脑，可以使用 *Downloader* 或 *Send Files to TV* 等侧载应用，将 APK 传到电视后打开安装。

## 如何镜像

在电视上打开 iMirror，并停留在等待连接界面。然后按以下方式操作：

- **iPhone 或 iPad：** 打开控制中心 →“屏幕镜像”→ 选择电视。
- **MacBook 或 iMac：** 打开控制中心 →“屏幕镜像”→ 选择电视。

<div align="center">

<img src="docs/macbook-screen-mirroring-to-android-tv.png" alt="macOS 屏幕镜像菜单中显示运行 iMirror 的 Android TV" width="480">

<sub>运行 iMirror 的 Android TV 会显示在 macOS 原生屏幕镜像菜单中，无需在 Mac 上安装额外软件。</sub>

</div>

电视会以“设置”中配置的名称显示在 AirPlay 设备列表中。手机或 Mac 无需安装任何应用。

> 手机和电视必须连接到**同一个 Wi-Fi 网络**。如果 Mac 会话在几秒后断开，可在设置中关闭
> *镜像音频（Mirror audio）*。macOS 对实时音频时钟同步的要求比 iOS 更严格；目前音频链路
> 的成熟度低于视频链路。

## 支持的设备

- **Android TV / Google TV：** Android 7.1（API 25）及以上
- **Amazon Fire TV：** Fire TV Stick 和 Fire TV Cube
- **手机和平板：** 同一 APK 也可安装运行，方便测试

发送端支持运行 macOS 12 或更新版本的 **iPhone、iPad 和 Mac**。

## 已知限制

- **Netflix、Disney+、Apple TV+ 无法镜像。** Apple 的 FairPlay DRM 会保护这些服务的内容。
  开源接收器无法解密；这不是 iMirror 的缺陷，也无法通过修复解决。
- **Apple Music 应用内音频**同样受保护；可以改为从 Mac 输出系统音频。
- **电视和发送端必须在同一网络。** Apple TV 可通过 Apple 的 AWDL 协议接受网络外连接，
  Android 硬件不支持 AWDL。
- **仅支持 H.264：** 尚未实现 HEVC。
- 如果路由器启用了**无线客户端隔离（AP isolation）或组播过滤**，电视可能不会出现在 AirPlay
  设备列表中，因为 mDNS 发现依赖组播。

## 从源码构建

在 x86_64 Linux 上安装 JDK 17、Android SDK Platform 35、Build Tools 34.0.0、
NDK `28.2.13676358` 和 CMake `3.22.1`。

```sh
git clone https://github.com/mrshyi/iMirror.git
cd iMirror
./gradlew assembleRelease
```

Gradle 会在 `app/build/outputs/apk/release/` 下生成各支持 ABI 的 Release APK，以及一个通用版
Release APK。Android Release APK 工作流会验证全部五个安装包。非 tag 的 CI 构建未签名；像
`v1.0.1-rc.1` 这样的预发布 tag 会使用专用预发布密钥签名，并随版本发布校验和清单和对应源码归档。

在 ARM64 Linux 上，请使用 GitHub Actions 中的 **Android Release APKs (all ABIs)** 工作流。
该工作流使用 x86_64 Ubuntu runner 运行 Google Linux 主机工具，并为四种原生 ABI 交叉编译。
分支推送、Pull Request 和手动运行会生成 Release variant 产物；非 tag 产物未签名。预发布 tag
会生成签名 APK，并将 APK、校验和与精确源码归档附加到 GitHub Release。Pull Request 产物应视为
不可信；安装时请使用可信 tag 发布的文件。CI 会检查 APK 内容，但电视上的安装和镜像仍需单独实机验证。

本地签名时，请通过 `KEYSTORE_PATH`、`KEYSTORE_PASSWORD`、`KEY_ALIAS` 和 `KEY_PASSWORD` 提供密钥信息。
CI 预发布工作流从仓库 Actions Secrets 读取这些凭据，并从 `RELEASE_KEYSTORE_BASE64` 读取 Base64 编码的密钥库。

## 工作原理

应用通过 mDNS 广播 `_airplay._tcp` 和 `_raop._tcp` 服务，并将自身标识为 Apple TV，使发送端选择镜像协议。
加密的 H.264 视频通过 TCP 传输，再由 `MediaCodec` 解码并直接输出到基于 `TextureView` 的显示表面。

> **贡献者注意：** 两个 mDNS 注册必须**串行执行**。Android 的 `NsdManager` 无法同时处理两个注册请求；
> 第二个请求可能会静默取消第一个，而且两个回调都不会触发。这个问题不会在日志中明显报错，最终导致 iOS
> 找不到电视。

## 许可证

**GPL-3.0**，详见 [LICENSE](LICENSE)。

iMirror 基于 [PhairPlay](https://github.com/mazer666/PhairPlay)（Apache-2.0）开发，并包含
[playfair](https://github.com/EstebanKubata/playfair) FairPlay 实现，该组件采用 GPL 许可，因此组合后的作品
也必须采用 GPL-3.0。第三方组件及完整归属信息见 [NOTICE.md](NOTICE.md)，二次分发前请阅读。

## 商标

AirPlay、Apple TV、iPhone、iPad、MacBook 和 macOS 均为 Apple Inc. 的商标。iMirror 与 Apple Inc. 无关联，
未获得其授权或认可。本项目仅为实现互操作性而实现相关协议。

---

<div align="center">

**关键词** — Android TV AirPlay 接收器 · iPhone 镜像到 Android TV · MacBook 投屏到电视 · iPad 屏幕镜像 ·
Google TV / Fire TV AirPlay · 免费投屏 APK · AirScreen 替代方案 · 无广告 AirPlay

</div>
