# java-ofd2jpg
OFD格式文档导出JPEG的命令行工具

# download
[ofd2jpg-1.0.0-all.jar](https://github.com/sssxyd/java-ofd2jpg/releases/download/1.0.0/ofd2jpg-1.0.0-all.jar)

# usage
## help
  ```shell
  java -jar ofd2jpg-1.0.0-all.jar -h
  ```
  ```text
  Usage: ofd2jpg [options] <OFD file>
  Options:
    -h, --help      Show this help message and exit
    -v, --version   Show version information and exit
    -i, --info      Show OFD DocInfo and exit
    -d, --dpi=DPI   Set the DPI for the output images (default: 128)
    -o, --output=DIR   Set the output directory for the converted images (default: ofd file directory)
  ```
## info
  ```shell
  java -Dfile.encoding=UTF-8 -jar ofd2jpg-1.0.0-all.jar -i xxx.ofd
  ```
  ```
  NumberOfPages: 1
  Fonts: 宋体, 楷体, Courier New
  Author: 
  CreationDate: 2025-01-06
  Creator: xxxx
  CreatorVersion: 1.0.0
  ModDate: 2024-10-22
  Subject: 
  Abstract: 
  Keywords: 
  CustomData: native-producer = SuwellFormSDK
  CustomData: producer-version = 1.1.22.0112
  CustomData: 发票号码 = 2542200000000357xxxx
  CustomData: 合计税额 = 2.61
  CustomData: 合计金额 = 261.39
  CustomData: 开票日期 = 2025年01月06日
  CustomData: 购买方纳税人识别号 = 91110302MA01xxxxxx
  CustomData: 销售方纳税人识别号 = 92420100MA4Jxxxxxx
  ```
## convert
   ```shell
   java -jar ofd2jpg-1.0.0-all.jar xxx.ofd
   # export to xxx.ofd.jpg
   ```
   
