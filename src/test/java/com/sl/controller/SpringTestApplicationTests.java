package com.sl.controller;

import com.qiniu.common.QiniuException;
import com.sl.service.IQiniuService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringTestApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Autowired
	private IQiniuService qiniuService;

	@Test
	public void testUpload() throws QiniuException {
		String result = qiniuService.uploadFile(new File("C:\\Users\\shili\\Pictures\\d20aa81f52384202b51b50a5b226c1b0 (2).jpeg"), "helloworld");
		System.out.println("访问地址： " + result);
	}

	@Test
	public void testDelete() throws QiniuException {
		String result = qiniuService.delete("helloworld");
		System.out.println(result);
	}


	@Test
	public void testDownload() throws QiniuException {
		String result = qiniuService.downloadPrivateFileUrl("http://q9nfrqzxn.bkt.clouddn.com/helloworld");
		System.out.println(result);
	}

}
