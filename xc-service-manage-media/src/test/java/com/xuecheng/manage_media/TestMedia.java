package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMedia {

    /**
     * 分块
     */
    @Test
    public  void  cut() throws IOException {
        //获取要分块的文件
        File sourceFile=new File("F:\\develop\\video\\lucene.mp4");
        //获取文件的大小
        long length = sourceFile.length();
        // 1M     1byte * 1024 = 1KB   1KB * 1024= 1MB
        long MB=1*1024*1024;
        //根据文件大小计算出 要分成多少个块文件   计算出的块数有小数  小数也算一个块文件 所以要用向上取整
        long count = Math.round(length * 1.0 / MB); // length / MB 的结果是整数没有小数   所以需要先将 length转换成浮点数 所以要 * 1.0
        //设置块文件保存的位置
        String path="F:\\develop\\video\\test\\";
        //读取资源文件   仅读
        int len=-1;
        //创建缓存区
        byte[] bytes=new byte[1024];
        RandomAccessFile read=new RandomAccessFile(sourceFile,"r");
        for (int i = 0; i < count; i++) {
            File file=new File(path+i);
            boolean newFile = file.createNewFile();
            RandomAccessFile write=new RandomAccessFile(file,"rw");
            if (newFile){
                //read.read(bytes)的意思  就是 读取资源文件的内容 并保存到 bytes字节数组中
                while ((len=read.read(bytes))!=-1){
                    //如果len!=-1 说明文件没读完 继续读取
                    write.write(bytes,0,len);
                    if (file.length()>=MB)
                        break;
                }
            }
            write.close();
        }
        read.close();
    }

    //合并块文件
    @Test
    public  void  hebin() throws IOException {
        //设置块文件的位置
        File chunk=new File("F:\\develop\\video\\test");
        //获取块文件集合
        File[] files = chunk.listFiles();
        //对块文件集合进行排序
        /**
         * public int compare(Object o1, Object o2)
         * 本来的顺序就是参数的先后顺序o1、o2；
         * 如果保持这个顺序就返回-1，交换顺序就返回1，什么都不做就返回0；
         * 所以 升序的话 如果o1<o2，就返回-1。
         */
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });
        //合并后的文件名称
        File file=new File("F:\\develop\\video\\lucene.avi");
        RandomAccessFile write=new RandomAccessFile(file,"rw");
        RandomAccessFile read=null;
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
        if (read!=null) {
            read.close();
        }
        write.close();
    }
}
