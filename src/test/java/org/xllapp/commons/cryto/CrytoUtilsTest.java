package org.xllapp.commons.cryto;

import org.junit.Assert;
import org.junit.Test;
import org.xllapp.commons.cryto.CrytoUtils;

/**
 *
 *
 * @Copyright: Copyright (c) 2013 FFCS All Rights Reserved 
 * @Company: 北京福富软件有限公司 
 * @author 陈作朋 Oct 17, 2013
 * @version 1.00.00
 * @history:
 * 
 */
public class CrytoUtilsTest {

	@Test
	public void testEncode() throws Exception{
		String key="MTIzNDU2NzgwYWJjZGVmZw";
		String expected="iHGD55SrIyvMPExDjDx0A72iRWJtSAz2%2Fe82b%2FkZJKcP%2FvBIus2yTBIVPtCVqTOcEa5caikNqeo%3D";
		String[] args={"2013-06-21 16:16:00",CrytoUtils.md5("abc","18900000","abc123","2013-06-21 16:16:00")};
		String actual=CrytoUtils.encode(key, args);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testDecode() throws Exception{
		String key="MTIzNDU2NzgwYWJjZGVmZw";
		String encStr="iHGD55SrIyvMPExDjDx0A72iRWJtSAz2%2Fe82b%2FkZJKcP%2FvBIus2yTBIVPtCVqTOcEa5caikNqeo%3D";
		String[] expecteds={"2013-06-21 16:16:00",CrytoUtils.md5("abc","18900000","abc123","2013-06-21 16:16:00")};
		String[] actuals = CrytoUtils.decode(key,encStr);
		Assert.assertArrayEquals(expecteds, actuals);
	}
	
	@Test
	public void testEncodeAndDecode() throws Exception{
		String key="MTIzNDU2NzgwYWJjZGVmZw";
		String[] args={"2013-06-21 16:16:00",CrytoUtils.md5("abc","18900000","abc123","2013-06-21 16:16:00")};
		String encStr=CrytoUtils.encode(key, args);
		String[] decArrays = CrytoUtils.decode(key,encStr);
		Assert.assertArrayEquals(args, decArrays);
	}

}
