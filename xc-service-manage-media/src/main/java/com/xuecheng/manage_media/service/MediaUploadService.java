package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MultipartFilter;

import java.io.*;
import java.util.*;

@Service
public class MediaUploadService {

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    private  String routingkey;
    @Value("${xc-service-manage-media.mq.queue-media-video-processor}")
    private String queuen_name;
    @Value("${xc-service-manage-media.upload-location}")
    private String upload_location;
    @Autowired
    private RabbitTemplate template;
    @Autowired
    private MediaFileRepository mediaFileRepository;
    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //判断媒资服务器和mongodb中是否存在 要上传的文件和文件信息
        String folderPath = getFolderPath(fileMd5);
        String filePath= getFilePath(fileMd5, fileExt);

        File file = new File(filePath);
        boolean exists = file.exists();
        //只有在服务器存在 并且 在MongoDB中存在 才认定为文件已存在  则抛出异常
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        if (exists==true&&optional.isPresent()){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        }
        //判断文件夹是否存在
        File file1 = new File(folderPath);
        if (!file1.exists()){
            file1.mkdirs();
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getFilePath(String fileMd5, String fileExt) {
        return getFolderPath(fileMd5)+fileMd5+"."+fileExt;
    }

    private String getFolderPath(String fileMd5) {
        //文件所在的文件夹路径
        String folderPath=upload_location+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
        return folderPath;
    }

    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Long chunkSize) {
        //拼接块文件所在路径
        String folderPath = getChunkFilePath(fileMd5);
        //判断块文件是否存在
        File file = new File(folderPath + chunk);
        if (file.exists()){
            return  new CheckChunkResult(CommonCode.SUCCESS,true);
        }
        return  new CheckChunkResult(CommonCode.SUCCESS,false);
    }

    private String getChunkFilePath(String fileMd5) {
        return getFolderPath(fileMd5)+"/chunks/";
    }

    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //1. 获取块文件所在目录   指定合并的文件目录
        String chunkFilePath = getChunkFilePath(fileMd5);
        File file = new File(chunkFilePath);
        File[] files = file.listFiles();

        String targetPath = getFilePath(fileMd5,fileExt);
        File targetFile=new File(targetPath);
        //合并文件
        File mergeFile = getMergeFile(targetFile, files);
        //校验md5
        boolean b = checkMd5(fileMd5, mergeFile);
        if (b==false){
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //将信息保存到 mongodb
        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        mediaFile.setFileStatus("301002");
        MediaFile save = mediaFileRepository.save(mediaFile);

        //发送消息
        Map<String,String> map=new HashMap();
        map.put("mediaId",fileMd5);
        String s = JSON.toJSONString(map);
        try {
            template.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey,s);
        } catch (AmqpException e) {
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getFileFolderRelativePath(String fileMd5) {
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/";
    }

    private  boolean checkMd5(String fileMd5,File mergeFile){
        //判断传过来的参数是否为空
        if(mergeFile == null || StringUtils.isEmpty(fileMd5)){
            return false;
        }
        FileInputStream mergeFileInputstream = null;

        try {
            //读取合并文件
            mergeFileInputstream=new FileInputStream(mergeFile);
            //获取文件的md5
            String s = DigestUtils.md5Hex(mergeFileInputstream);
            if (fileMd5.equals(s)){
                return  true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  false;
    }
    private File getMergeFile(File file, File[] files) {
        if (file.exists()){
            file.delete();
        }
        else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RandomAccessFile write= null;
        RandomAccessFile read=null;
        try {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                        return 1;
                    }
                    return -1;
                }
            });
            write = new RandomAccessFile(file,"rw");
            byte[] bytes=new byte[1024];
            int len=-1;
            //进行合并 读取块文件
            for (File file1 : files) {
                //将块文件的内容读出
                    read=new RandomAccessFile(file1,"r");
                    while ((len=read.read(bytes))!=-1){
                        //将读取的内容输出到目标文件
                        write.write(bytes,0,len);
                    }
            }
            return  file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                read.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }

    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5) {
        //上传过来的是一个一个的块文件
        String chunkFilePath = getChunkFilePath(fileMd5);
        File file1 = new File(chunkFilePath);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File chunkfile = new File( chunkFilePath+ chunk); //指定输出文件
        OutputStream outputStream=null;
        InputStream inputStream=null;
        try {
            inputStream= file.getInputStream();
            outputStream=new FileOutputStream(chunkfile);
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }
}
