package com.fastdfs.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * @author liaoyubo
 * @version 1.0 2017/9/18
 * @description
 */
@RestController
public class UpDownController {

    @Autowired
    @Resource(name = "myStorageClient1")
    private StorageClient1 storageClient1;

    /**
     * 上传文件
     * @param multipartFile
     * @param httpServletRequest
     * @throws IOException
     * @throws MyException
     */
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public void upload(MultipartFile multipartFile, HttpServletRequest httpServletRequest) throws IOException, MyException {
        String fileExtName = multipartFile.getOriginalFilename().split("\\.")[1];
        String fileName = multipartFile.getOriginalFilename().split("\\.")[0];
        byte[] bytes = multipartFile.getBytes();
        // 拼接服务器的文件路径
        StringBuffer address = new StringBuffer();
        address.append("http://127.0.0.1");
        //设置文件元数据
        Map<String,String> metaList =  new HashMap<String, String>();
        metaList.put("fileName",fileName);
        metaList.put("fileExtName",fileExtName);
        NameValuePair[] nameValuePairs = null;
        if (metaList != null) {
            nameValuePairs = new NameValuePair[metaList.size()];
            int index = 0;
            for (Iterator<Map.Entry<String,String>> iterator = metaList.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,String> entry = iterator.next();
                String name = entry.getKey();
                String value = entry.getValue();
                nameValuePairs[index++] = new NameValuePair(name,value);
            }
        }

        //利用字节流上传文件
        String strings = storageClient1.upload_file1(bytes, fileExtName,nameValuePairs);
        address.append("/" + strings);
        // 全路径
        System.out.println(address);
    }

    @RequestMapping("/testUpload")
    public void upload(){
        String[] strings;
        //第二个参数必须是文件的后缀名
        try {
            strings = storageClient1.upload_file("E:\\12.jpg", "jpg", null);
            for (String string : strings) {
                System.out.println(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件元数据
     * @param fileId
     * @return
     */
    public  Map<String,String> getMetaList(String fileId){
        try {
            NameValuePair[] metaList = storageClient1.get_metadata1(fileId);
            if (metaList != null) {
                HashMap<String,String> map = new HashMap<String, String>();
                for (NameValuePair metaItem : metaList) {
                    map.put(metaItem.getName(),metaItem.getValue());
                }
                return map;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     * @param fileId
     * @return 删除失败返回-1，否则返回0
     */
    @RequestMapping(value = "/delFile",method = RequestMethod.DELETE)
    public int delFile(@RequestParam String fileId){
        try {
            return storageClient1.delete_file1(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @RequestMapping(value = "/downloadFile",method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileId){
        byte[] content = null;
        HttpHeaders headers = new HttpHeaders();
        try {
            Map<String,String> metaMap = getMetaList(fileId);
            content = storageClient1.download_file1(fileId);
            headers.setContentDispositionFormData("attachment",  new String(metaMap.get("fileName").getBytes("UTF-8"),"iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<byte[]>(content, headers, HttpStatus.CREATED);
    }

    @RequestMapping("/testDown")
    public void download(){
        byte[] b = new byte[0];
        try {
            b = storageClient1.download_file("group1", "M00/00/00/wKjJtVnIef-AIHo_AAIn_DuAAjs682.jpg");
            System.out.println(b.length);
            String uuid = UUID.randomUUID().toString();
            System.out.println(uuid);
            // 将下载的文件流保存
            IOUtils.write(b, new FileOutputStream("E:/"+ uuid +".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

}
