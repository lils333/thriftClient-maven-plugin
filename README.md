# thriftClient-maven-plugin
## This is a common thrift client plugin, use ASM to generate thrift client code
1. generate retry polcy use Failsafe
2. generate CircuitBreaker use Failsafe
3. generate pool use commons-pool2 to cache thrift client
  
# how to use
1.在项目里面配置插件
```xml
<plugin>
      <groupId>com.nsn</groupId>
      <artifactId>thrift-maven-plugin</artifactId>
      <version>1.0</version>
      <configuration>
          <clients>
               <!-- 这个地方随便指定，只是一个包含一个空方法体的类 -->
              <client>com.lee.thrift.client.RpcClient</client>
          </clients>
      </configuration>
      <executions>
          <execution>
              <goals>
                  <goal>thriftclient</goal>
              </goals>
              <phase>compile</phase>
          </execution>
      </executions>
      <dependencies>
             <!-- 这个地方添加thrift代码所在的jar包，如果thrift代码就在本地，那么就加载当前项目里面的 -->
      </dependencies>
  </plugin>
```     
     
2.在 插件指定的类里面添加一个空方法
```java
package com.lee.thrift.client;
import com.lee.thrift.MyFirstService;

public class RpcClient {
  //这个地方需要制定返回值，只需要指定接口就可以了，代码会自动寻找并添加
  public MyFirstService.Iface getMyFirstService(boolean isFramed,
                                              String address, 
                                              int port,
                                              int connectionTimeout,
                                              int socketTimeout) {
    return null;
  }
} 
```      
