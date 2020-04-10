package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class jwt {


    @Test
    public  void  createJWT(){
        //1. 准备信息
        String alias="xckey";
        String keyPassword="xuecheng";
        Resource resource=new ClassPathResource("xc.keystore");
        String keystoreName="xc.keystore";
        String keystorePassword="xuechengkeystore";

        //2. 创建密钥工厂 相当于 进入了 密钥库
        KeyStoreKeyFactory keyStoreKeyFactory=new KeyStoreKeyFactory(resource,keystorePassword.toCharArray());
        //3. 获取密钥库中  指定密钥的密钥对  需要 密钥名称和密钥密码
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keyPassword.toCharArray());
        //获取 公钥 和 私钥
        PrivateKey aPrivate = keyPair.getPrivate();
        PublicKey aPublic = keyPair.getPublic();
        //4. 通过工具类生成 jwt 令牌  传入内容和 指定加密算法 指定私钥进行加密
        Map map=new HashMap();
        map.put("name","zzh");
        String s = JSON.toJSONString(map);
        Jwt jwt = JwtHelper.encode(s, new RsaSigner((RSAPrivateKey) aPrivate));
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }

    @Test
    public  void  testVerify(){
        String publickey="-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjNLXtEUVn5lpTy5xXlFZscynXZ596KGwV1CvnV78TPh4R2mA+ojVcp/rkWy22C/AHKbEtoMloHwWt8hI6vG1sh759t1Bm3qWgEyPl7Kb1HYWCfr7Pszr0lpFfdukTzkHMkHFO6ro6tIM5baOx+sioI/n1zO1towVO/jb+42VEH4f4otIsKnrxjaZdhFhLHRybxu946TBUTcTo73elIW4Q72r2V+K34QlsttKwFhIzkOBlA2le59VrYeLgGMr8DYjpzNVQ6D53KOuck/pcrX8JWSYb1SmBDouXee+OG3BbE6Jog1ocJ5SBsywGj8nUQwrxfOAQ93DkKXX0WtREhqyPwIDAQAB-----END PUBLIC KEY-----";
        String  token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOiIxIiwidXNlcnBpYyI6bnVsbCwidXNlcl9uYW1lIjoiaXRjYXN0Iiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOiJ0ZXN0MDIiLCJ1dHlwZSI6IjEwMTAwMiIsImlkIjoiNDkiLCJleHAiOjE1ODYyMDQ3NjIsImF1dGhvcml0aWVzIjpbInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYmFzZSIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfZGVsIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9saXN0IiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9wbGFuIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZSIsImNvdXJzZV9maW5kX2xpc3QiLCJ4Y190ZWFjaG1hbmFnZXIiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX21hcmtldCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcHVibGlzaCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfYWRkIl0sImp0aSI6ImU1ZWI2MDVhLTE0MWQtNDU4My1hMmRlLWY3MmZkNGNhYjBhZSIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.fn496KG28vCKCpeCaaoafm1U6314pIWFc9ceUmgJmgkhcRTCYz_FcEywcqYs97kfcSctM6l8wfN2DFwcfREONL0BJgF3LjZ3Uv4NQvE3KAupCRjqaOIaK6XZ9y-LHawM0--FAXESdYr6_T0G_oRZQeG9b-v0Fm5DLbqoBxfyVURw2cDaTM76HvEr8Yi-zl5F1sTQwJ8nj8aFa8pUBXqtfl85KB-eEshW1yW6j1MKp04upRXsmsTzDs-iewtjhAJr_4diyScX4P4g3wIzvwfsrYnIWjHlvYkZXX_3qWGjrw0YmmEFwPjCgPEOd9r1g4JSJrMIjAVmuQ2CrDhDS3EohA";
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
        System.out.println(jwt.getClaims());
    }


}
