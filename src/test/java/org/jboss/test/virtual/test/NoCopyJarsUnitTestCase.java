/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.virtual.test;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.jboss.test.BaseTestCase;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

/**
 * Tests of no copy nested jars
 * 
 * @author ales.justin@jboss.org
 * @author Scott.Stark@jboss.org
 * @author adrian@jboss.org
 * @version $Revision$
 */
public class NoCopyJarsUnitTestCase extends BaseTestCase
{
   public NoCopyJarsUnitTestCase(String name)
   {
      super(name);
   }
   
   public static Test suite()
   {
      return new TestSuite(NoCopyJarsUnitTestCase.class);
   }

   
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      System.setProperty("jboss.vfs.forceNoCopy", "true");
   }

   /**
    * Test reading the contents of nested jar entries.
    * @throws Exception
    */
   public void testInnerJarFile() throws Exception
   {
      URL rootURL = getResource("/vfs/test");
      VFS vfs = VFS.getVFS(rootURL);
      VirtualFile outerjar = vfs.findChild("outer.jar");
      assertTrue("outer.jar != null", outerjar != null);
      VirtualFile jar1 = outerjar.findChild("jar1.jar");
      assertTrue("outer.jar/jar1.jar != null", jar1 != null);
      VirtualFile jar2 = outerjar.findChild("jar2.jar");
      assertTrue("outer.jar/jar2.jar != null", jar2 != null);

      VirtualFile jar1MF = jar1.findChild("META-INF/MANIFEST.MF");
      assertNotNull("jar1!/META-INF/MANIFEST.MF", jar1MF);
      InputStream mfIS = jar1MF.openStream();
      Manifest mf1 = new Manifest(mfIS);
      Attributes mainAttrs1 = mf1.getMainAttributes();
      String title1 = mainAttrs1.getValue(Attributes.Name.SPECIFICATION_TITLE);
      assertEquals("jar1", title1);
      jar1MF.close();

      VirtualFile jar2MF = jar2.findChild("META-INF/MANIFEST.MF");
      assertNotNull("jar2!/META-INF/MANIFEST.MF", jar2MF);
      InputStream mfIS2 = jar2MF.openStream();
      Manifest mf2 = new Manifest(mfIS2);
      Attributes mainAttrs2 = mf2.getMainAttributes();
      String title2 = mainAttrs2.getValue(Attributes.Name.SPECIFICATION_TITLE);
      assertEquals("jar2", title2);
      jar2MF.close();
   }

   public void testInnerJarFileSerialization() throws Exception
   {
      URL rootURL = getResource("/vfs/test");
      VFS vfs = VFS.getVFS(rootURL);
      VirtualFile outerjar = vfs.findChild("outer.jar");
      assertTrue("outer.jar != null", outerjar != null);
      log.info("outer.jar: "+outerjar);
      VirtualFile jar1 = outerjar.findChild("jar1.jar");
      assertTrue("outer.jar/jar1.jar != null", jar1 != null);
      VirtualFile jar2 = outerjar.findChild("jar2.jar");
      assertTrue("outer.jar/jar2.jar != null", jar2 != null);
   
      VirtualFile jar1MF = jar1.findChild("META-INF/MANIFEST.MF");
      assertNotNull("jar1!/META-INF/MANIFEST.MF", jar1MF);
      InputStream mfIS = jar1MF.openStream();
      Manifest mf1 = new Manifest(mfIS);
      Attributes mainAttrs1 = mf1.getMainAttributes();
      String title1 = mainAttrs1.getValue(Attributes.Name.SPECIFICATION_TITLE);
      assertEquals("jar1", title1);
      jar1MF.close();
   
      VirtualFile jar1DS = serializeDeserialize(jar1, VirtualFile.class);
      assertNotNull("jar1 deserialized", jar1DS);
      VirtualFile jar1DSMF = jar1.findChild("META-INF/MANIFEST.MF");
      mfIS = jar1DSMF.openStream();
      mf1 = new Manifest(mfIS);
      mainAttrs1 = mf1.getMainAttributes();
      title1 = mainAttrs1.getValue(Attributes.Name.SPECIFICATION_TITLE);
      assertEquals("jar1", title1);
      jar1DSMF.close();
   }
   /**
    * JBVFS-17 test
    * @throws Exception
    */
   public void testInnerJarFilesOnlyFileSerialization() throws Exception
   {
      URL rootURL = getResource("/vfs/test");
      VFS vfs = VFS.getVFS(rootURL);
      VirtualFile outerjar = vfs.findChild("outer.jar");
      assertTrue("outer.jar != null", outerjar != null);
      log.info("outer.jar: "+outerjar);
      VirtualFile jar1 = outerjar.findChild("jar1-filesonly.jar");
      assertTrue("outer.jar/jar1-filesonly.jar != null", jar1 != null);
   
      VirtualFile jar1MF = jar1.findChild("META-INF/MANIFEST.MF");
      assertNotNull("jar1-filesonly!/META-INF/MANIFEST.MF", jar1MF);
      InputStream mfIS = jar1MF.openStream();
      Manifest mf1 = new Manifest(mfIS);
      Attributes mainAttrs1 = mf1.getMainAttributes();
      String title1 = mainAttrs1.getValue(Attributes.Name.SPECIFICATION_TITLE);
      assertEquals("jar1-filesonly", title1);
      jar1MF.close();
   
      VirtualFile jar1DS = serializeDeserialize(jar1, VirtualFile.class);
      assertNotNull("jar1 deserialized", jar1DS);
      VirtualFile jar1DSMF = jar1DS.getChild("META-INF/MANIFEST.MF");
      assertNotNull("jar1-filesonly!/META-INF/MANIFEST.MF", jar1DSMF);
      mfIS = jar1DSMF.openStream();
      mf1 = new Manifest(mfIS);
      mainAttrs1 = mf1.getMainAttributes();
      title1 = mainAttrs1.getValue(Attributes.Name.SPECIFICATION_TITLE);
      assertEquals("jar1-filesonly", title1);
      jar1DSMF.close();
   }

   public void testLevelZips() throws Exception
   {
/*
      URL rootURL = getResource("/vfs/test");
      VFS vfs = VFS.getVFS(rootURL);
      VirtualFile one = vfs.findChild("level1.zip");
      VirtualFile textOne = one.findChild("test1.txt");
      testText(textOne);
      VirtualFile two = one.findChild("level2.zip");
      VirtualFile textTwo = two.findChild("test2.txt");
      testText(textTwo);
      VirtualFile three = two.findChild("level3.zip");
      VirtualFile textThree = three.findChild("test3.txt");
      testText(textThree);

      three = serializeDeserialize(three, VirtualFile.class);
      textThree = three.findChild("test3.txt");
      testText(textThree);

      two = serializeDeserialize(two, VirtualFile.class);
      textTwo = two.findChild("test2.txt");
      testText(textTwo);
      three = two.findChild("level3.zip");
      textThree = two.findChild("level3.zip/test3.txt");
      testText(textThree);
      textThree = three.findChild("test3.txt");
      testText(textThree);

      one = serializeDeserialize(one, VirtualFile.class);
      textOne = one.findChild("test1.txt");
      testText(textOne);
      two = one.findChild("level2.zip");
      textTwo = one.findChild("level2.zip/test2.txt");
      testText(textTwo);
      textTwo = two.findChild("test2.txt");
      testText(textTwo);
      three = one.findChild("level2.zip/level3.zip");
      textThree = three.findChild("test3.txt");
      testText(textThree);
      textThree = one.findChild("level2.zip/level3.zip/test3.txt");
      testText(textThree);
      three = two.findChild("level3.zip");
      textThree = three.findChild("test3.txt");
      testText(textThree);
      textThree = two.findChild("level3.zip/test3.txt");
      testText(textThree);
*/
   }

   protected void testText(VirtualFile file) throws Exception
   {
      InputStream in = file.openStream();
      try
      {
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         String line;
         while ((line = reader.readLine()) != null)
         {
            assertEquals("Some test.", line);
         }
      }
      finally
      {
         in.close();
      }
   }

}