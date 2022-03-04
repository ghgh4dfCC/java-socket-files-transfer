## 基于Java socket的自定义协议文件/指令传输组件

用途：不同主机之间的文件/文件夹传输和远程执行命令行，实现不同操作系统之间（比如windows对windows、windows对linux、linux对linux、linux对windows）的单文件传输、目录/多文件传输、远程执行命令行，主要用来解决jenkins持续集成中复杂另类的文件传输需求。

### 使用方法：

**安装接收端**

对源码使用maven命令编译后，得到编译产物后，根据不同操作系统可选择使用代码项目中的**startup-linux.sh**或**startup-win.cmd**来启动接收端，在windows系统下，可以使用 NSSM 将jar程序封装成windows服务，方便管理接收端程序。

接收端启动无误后，需要在即将使用发送端的机器上，使用命令行 "telnet 接收端ip 端口号"，来检查接收端是否已可以连接，如果telnet不上，需检查接收端程序是否正确启动，或者防火墙等相关配置


**发送端的使用说明**

注意：下面的命令行都需要先切换当前目录到TcpFilesTransfer的jar包所在目录

### 传输单文件

命令格式：

java -cp ".;\*;lib/\*" com.filestransfer.sender.TransferSender -port 接收端端口号 -ip 接收端ip -file "需要传输的文件的全路径" -targetPath "你所希望该文件放置的接收端/目标机器的目录"

例子：

**发送端是windows**: 

java -cp ".;\*;lib/\*" com.filestransfer.sender.TransferSender -port 8586 -ip 192.168.1.100 -file "D:\filetest01\中文文件名测试.txt" -targetPath "D:\FTCache3"

**发送端是linux**: 

java -cp "classes:./\*:lib/\*" com.filestransfer.sender.TransferSender -port 8586 -ip 192.168.1.100 -file "/data/TcpFilesTransferTest/中文.txt" -targetPath "D:\FTCache3"

### 传输整个目录

命令格式：

java -cp ".;\*;lib/\*" com.filestransfer.sender.TransferSender -port 接收端端口号 -ip 接收端ip -dir "需要传输的目录的全路径" -targetPath "你所希望该目录下的文件的放置的接收端/目标机器的目录"

例子：

**发送端是windows**: 

java -cp ".;\*;lib/\*" com.filestransfer.sender.TransferSender -port 8586 -ip 192.168.1.100 -dir "D:\filetest01" -targetPath "D:\FTCache3"

**发送端是linux**: 

java -cp "classes:./\*:lib/\*" com.filestransfer.sender.TransferSender -port 8586 -ip 192.168.1.100 -file "/data/TcpFilesTransferTest" -targetPath "D:\FTCache3"

目录传输目前支持根据文件后缀名过滤需要传输的文件，使用方法是 在上面的命令行中 加上 -filter 参数，例如 -filter ".config"、-filter ".config.xml"


### 远程执行命令

**发送端是windows**: 

命令格式：

java -cp ".;\*;lib/\*" com.filestransfer.sender.TransferSender -port 接收端端口号 -ip 接收端ip -shell "你需要在目标机器执行的命令脚本"

例子：

列出目标机器上 F 盘下的目录：

java -cp ".;\*;lib/\*" com.filestransfer.sender.TransferSender -port 8586 -ip 192.168.1.100 -shell "dir f:\"

**发送端是linux**: 

命令格式：

java -cp "classes:./\*:lib/\*" com.filestransfer.sender.TransferSender -port 接收端端口号 -ip 接收端ip -shell "你需要在目标机器执行的命令脚本"

例子：

在目标机器上输出日期

java -cp "classes:./\*:lib/\*" com.filestransfer.sender.TransferSender -port 8586 -ip 192.168.1.100 -shell "date"


### 测试结果
文件传输组件性能测试结果（局域网）：

**单文件传输：**

70M文件 传输耗时：8s  速度：70/8=8.75m/s

1.7G(1740M)文件传输耗时：212s  速度：1740/212=8.21m/s

**整个目录多文件传输：**

比如静态资源的 master分支 ，文件总大小942M，文件数12183，文件夹数1107，传输耗时 178s，速度：942/178=5.3m/s

文件/文件夹全部 传输成功，没有丢失

如有问题，请在Issues中反馈，谢谢！


