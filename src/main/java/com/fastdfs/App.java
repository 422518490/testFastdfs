package com.fastdfs;

import org.csource.fastdfs.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author liaoyubo
 * @version 1.0 2017/9/18
 * @description
 */
@SpringBootApplication
public class App {

    public static void main(String [] args){
        SpringApplication.run(App.class,args);
    }

    @Bean(name = "myStorageClient1")
    public StorageClient1 storageClient1(){
        try {
            // 初始化文件资源
            ClientGlobal.init("F:\\testFastdfs\\src\\main\\resources\\fdfs_client.conf");
            // 链接FastDFS服务器，创建tracker和Storage
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            if(trackerServer == null){
                throw new IllegalStateException("无法连接到跟踪服务器");
            }
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            if(storageServer == null){
                throw new IllegalStateException("无法连接到存储服务器");
            }
            return new StorageClient1(trackerServer,storageServer);
        }catch (Exception e){
        }
        return null;
    }

}
