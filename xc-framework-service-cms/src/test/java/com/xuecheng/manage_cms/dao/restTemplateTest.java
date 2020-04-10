package com.xuecheng.manage_cms.dao;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.manage_cms.service.CmsPageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class restTemplateTest {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private  cmsTemplateRepositry cmsTemplateRepositry;

    @Autowired
    private  CmsPageService cmsPageService;
    @Autowired
    private RestTemplate restTemplate;


    @Test
    public  void test01(){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        System.out.println(body);
    }


    @Test
    //静态化页面输出
    public  void createHTML(){
        //获取模型数据
        Map body = getModelByPageId("5e704de091e75e1d0c4ece00");
        //通过configuration设置模板信息，并合并内容 输出文件
        String content = OutputHtml(body,"C:\\Users\\user\\Desktop\\fmtest01.html","5e704de091e75e1d0c4ece00");

        System.out.println(content);

    }

    private Map getModelByPageId(String pageId) {
        //通过PageId  查询dataUrl
        CmsPage cmsPage = cmsPageService.findCmsPageById(pageId);
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl))
            ExceptionCast.cast(CmsCode.CMS_PARAMS_NULL);

        //通过RestTemplate发起请求，返回模型数据
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        return entity.getBody();
    }

    private String OutputHtml(Map body,String FilePath,String pageId) {
        try {
            //查询cmsPage
            CmsPage cmsPage = cmsPageService.findCmsPageById(pageId);
            String templateId = cmsPage.getTemplateId();
            if(StringUtils.isEmpty(templateId)){
                //页面模板为空
                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            }
            CmsTemplate cmsTemplate = cmsTemplateRepositry.findByTemplateFileId(templateId);
            if (cmsTemplate==null){
                ExceptionCast.cast(CmsCode.CMS_PARAMS_NULL);
            }
                //获取模板文件id
                String templateFileId = cmsTemplate.getTemplateFileId();

                //创建查询条件 通过Query.query()   传入一个Criteria
                Query query = Query.query(Criteria.where("_id").is(templateFileId));
                GridFSFile gridFSFile = gridFsTemplate.findOne(query);

                //打开一个下载流对象
                GridFSDownloadStream stream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
                //获取流
                GridFsResource gridFsResource = new GridFsResource(gridFSFile,stream);
                //返回模板内容的字符串格式
                String s = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");

                // 通过configuration设置模板相关信息
                Configuration configuration = new Configuration(Configuration.getVersion());
                //创建字符串模板加载器
                StringTemplateLoader stringTemplateLoader=new StringTemplateLoader();
                stringTemplateLoader.putTemplate("fmtest01",s);
                //将字符串模板加载器添加到configuration中
                configuration.setTemplateLoader(stringTemplateLoader);
                Template template = configuration.getTemplate("fmtest01");
                // 将模板和数据合并 输出
                String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, body);
                //创建输入流和输出流 输入流将内容读取到内存中  输出流指定输出的文件路径和名称
                InputStream inputStream = IOUtils.toInputStream(content, "utf-8");
                FileOutputStream fileOutputStream = new FileOutputStream(FilePath);
                //通过IOUtils.copy()将内存中的内容输出到指定路径
                IOUtils.copy(inputStream, fileOutputStream);
                //关闭流
                inputStream.close();
                fileOutputStream.close();
                //判断合成
                if (StringUtils.isBlank(content)) {
                    ExceptionCast.cast(CmsCode.CREATE_HTML_FAIL);
                }
                return content;


        } catch (Exception e) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_SAVEHTMLERROR);
        }
        return null;
    }


    @Test
    public  void test(){
        Optional<CmsTemplate> optional = cmsTemplateRepositry.findById("5e706e34005ed850308b2bca");
        if (optional.isPresent()){
            System.out.println(1);
        }
        else System.out.println(2);
    }
}
