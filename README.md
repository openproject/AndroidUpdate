[![Jcenter Status](https://api.bintray.com/packages/openproject/maven/lesscode-update/images/download.svg)](https://bintray.com/openproject/maven/lesscode-update)

# LessCode-Update
the best android check update library!

## Gradle

```groovy
compile('com.jayfeng:lesscode-update:1.0');
```

## Overview
> * 一键集成，代码少，简单
> * 稳定，考虑周全，经过商业验证
> * 兼容Android7.0+
> * 开源

## Usage
基本配置继承自LessCode：
```groovy
$.getInstance()
    .update(null, 1, R.mipmap.ic_launcher)   // 自定义下载图标
    .build();
```
为了兼容Android 7.0+的FileProvider，而Provider是不能冲突的，所以必须自定义这个Provider的author:
```groovy
android {
    defaultConfig {
        resValue "string", "less_provider_file_authorities", "<packageName>.fileprovider"
    }
}
```
切记！切记！一定要加！不支持Android7.0+的也要加！

## Author

> Author weibo：<a href="http://weibo.com/xiaofengjian" target="_blank">冯建V</a>&nbsp;&nbsp;&nbsp;&nbsp;mail：673592063@qq.com&nbsp;&nbsp;&nbsp;&nbsp;QQ：673592063

## License

```
Copyright (C)  LessCode Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
