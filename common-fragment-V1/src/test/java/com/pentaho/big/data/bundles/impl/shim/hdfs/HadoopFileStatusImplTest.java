/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package com.pentaho.big.data.bundles.impl.shim.hdfs;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/3/15.
 */
public class HadoopFileStatusImplTest {
  private FileStatus fileStatus;
  private HadoopFileStatusImpl hadoopFileStatus;

  @Before
  public void setup() {
    fileStatus = mock( FileStatus.class );
    hadoopFileStatus = new HadoopFileStatusImpl( fileStatus );
  }

  @Test
  public void testGetLen() {
    long len = 11111L;
    when( fileStatus.getLen() ).thenReturn( len );
    assertEquals( len, hadoopFileStatus.getLen() );
  }

  @Test
  public void testIsDir() {
    when( fileStatus.isDir() ).thenReturn( true ).thenReturn( false );
    assertTrue( hadoopFileStatus.isDir() );
    assertFalse( hadoopFileStatus.isDir() );
  }

  @Test
  public void testGetModificationTime() {
    long modificationTime = 11112L;
    when( fileStatus.getModificationTime() ).thenReturn( modificationTime );
    assertEquals( modificationTime, hadoopFileStatus.getModificationTime() );
  }

  @Test
  public void testGetPath() {
    String pathString = "test/path/here";
    when( fileStatus.getPath() ).thenReturn( new Path( pathString ) );
    assertEquals( pathString, hadoopFileStatus.getPath().getPath() );
  }
}
