package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.framework.utils.VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class mqListen {

    @Value("${xc-service-manage-media.video-location}")
    private  String serverpath;
    @Value("${xc-service-manage-media.ffmpeg-path}")
    private  String ffmpegpath;
    @Autowired
    private MediaFileRepository mediaFileRepository;
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg){
        //解析msg  获取 mediaId
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        //通过id查询mongodb
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()){
            return;
        }
        MediaFile mediaFile = optional.get();
        //需要先判断上传文件的类型  目前只支持 avi格式
        if (mediaFile.getFileType().equals("avi")){
            mediaFile.setProcessStatus("301001"); //处理中
            mediaFileRepository.save(mediaFile);
        }
        else {
            mediaFile.setProcessStatus("303004"); //无需处理
            mediaFileRepository.save(mediaFile);
            return;
        }
        String filePath = mediaFile.getFilePath();
        String video_path=serverpath+filePath+mediaFile.getFileName();
        String mp4_name=mediaId+".mp4";
        String mp4_folderpath=serverpath+filePath;
        File file=new File(mp4_folderpath);
        if (!file.exists()){
            file.mkdirs();
        }
        Mp4VideoUtil mp4VideoUtil=new Mp4VideoUtil(ffmpegpath,video_path,mp4_name,mp4_folderpath);
        String result = mp4VideoUtil.generateMp4();
        if (result==null||!result.equals("success")){
            //AVI转换成MP4失败
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }

        //将转换成功的mp4转换成m3u8
        String m3u8_name=mediaId+".m3u8";
        String m3u8_folderpath=serverpath+filePath+"hls/";
        HlsVideoUtil hlsVideoUtil=new HlsVideoUtil(ffmpegpath,mp4_folderpath+mp4_name,m3u8_name,m3u8_folderpath);
        String generateM3u8 = hlsVideoUtil.generateM3u8();
        if (generateM3u8==null||!generateM3u8.equals("success")){
            //生成m3u8失败
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        //mp4转换成 m3u8成功  获取ts文件列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        mediaFile.setFileUrl(mediaFile.getFilePath()+"hls/"+m3u8_name);
        mediaFile.setProcessStatus("303002");
        mediaFileRepository.save(mediaFile);
    }
}
