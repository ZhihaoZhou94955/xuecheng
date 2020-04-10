package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

    @Test
    public void testProcessBuilder() throws IOException {

        //创建ProcessBuilder对象
        ProcessBuilder processBuilder =new ProcessBuilder();
        //设置执行的第三方程序(命令)
//        processBuilder.command("ping","127.0.0.1");
        processBuilder.command("ipconfig");
//        processBuilder.command("java","-jar","f:/xc-service-manage-course.jar");
        //将标准输入流和错误输入流合并，通过标准输入流读取信息就可以拿到第三方程序输出的错误信息、正常信息
        processBuilder.redirectErrorStream(true);

        //启动一个进程
        Process process = processBuilder.start();
        //由于前边将错误和正常信息合并在输入流，只读取输入流
        InputStream inputStream = process.getInputStream();
        //将字节流转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream,"gbk");
       //字符缓冲区
        char[] chars = new char[1024];
        int len = -1;
        while((len = reader.read(chars))!=-1){
            String string = new String(chars,0,len);
            System.out.println(string);
        }

        inputStream.close();
        reader.close();

    }

    //测试使用工具类将avi转成mp4
    @Test
    public void testProcessMp4(){
        //String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        //ffmpeg的路径
        String ffmpeg_path = "D:\\Program Files\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        //video_path视频地址
        String video_path = "E:\\ffmpeg_test\\1.avi";
        //mp4_name mp4文件名称
        String mp4_name  ="1.mp4";
        //mp4folder_path mp4文件目录路径
        String mp4folder_path="E:/ffmpeg_test/";
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        //开始编码,如果成功返回success，否则返回输出的日志
        String result = mp4VideoUtil.generateMp4();
        System.out.println(result);
    }


    @Test
    public  void testprocess() throws IOException {
        //创建processBuilder
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置 执行 指令
//        processBuilder.command("ping","127.0.0.1");
        processBuilder.command("ipconfig");
        //将 标准输入流 和 错误流 合并
        processBuilder.redirectErrorStream(true);
        //启动进程 执行指令
        Process start = processBuilder.start();
        //因为 标准输入流和错误流合并了  所以直接取 输入流
        InputStream inputStream = start.getInputStream();
        //通过 字符流来获取 执行指令产生的结果  在字符流中指定编码格式
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"gbk");
        char[] chars=new char[1024];
        int len=-1;
        while ((len=inputStreamReader.read(chars))!=-1){
            String s=new String(chars,0,len);
            System.out.println(s);
        }


    }


    @Test
    public  void testconverttomp4() throws IOException {
        //创建processBuilder
        ProcessBuilder processBuilder = new ProcessBuilder();
        //设置 执行 指令
        List<String> strings=new ArrayList<>();
        strings.add("I:/ffmpeg-20180227-fa0c9d6-win64-static/bin/ffmpeg.exe");
        strings.add("-i");
        strings.add("F:\\develop\\video\\lucene.avi");
        strings.add("-c:v"); //视频编码格式
        strings.add("libx264");
        strings.add("-s");
        strings.add("1280x720");
        strings.add("-pix_fmt");
        strings.add("yuv420p");
        strings.add("-b:a");
        strings.add("63k");
        strings.add("-b:v");
        strings.add("753k");
        strings.add("-r");
        strings.add("18");
        strings.add("F:\\develop\\video\\lucene.mp4");
        processBuilder.command(strings);
        //将 标准输入流 和 错误流 合并
        processBuilder.redirectErrorStream(true);
        //启动进程 执行指令
        Process start = processBuilder.start();
        //因为 标准输入流和错误流合并了  所以直接取 输入流
        InputStream inputStream = start.getInputStream();
        //通过 字符流来获取 执行指令产生的结果  在字符流中指定编码格式
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"gbk");
        char[] chars=new char[1024];
        int len=-1;
        while ((len=inputStreamReader.read(chars))!=-1){
            String s=new String(chars,0,len);
            System.out.println(s);
        }


    }
}
