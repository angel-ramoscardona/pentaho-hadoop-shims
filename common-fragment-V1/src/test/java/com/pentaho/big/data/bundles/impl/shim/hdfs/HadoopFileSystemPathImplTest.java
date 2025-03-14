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

import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bryan on 8/3/15.
 */
public class HadoopFileSystemPathImplTest {
  private Path path;
  private HadoopFileSystemPathImpl hadoopFileSystemPath;
  private URI uri;

  @Before
  public void setup() throws URISyntaxException {
    path = mock( Path.class );
    uri = new URI( "hdfs://test:8080/test" );
    when( path.toUri() ).thenReturn( uri );
    hadoopFileSystemPath = new HadoopFileSystemPathImpl( path );
  }

  @Test
  public void testGetPath() {
    assertEquals( "/test", hadoopFileSystemPath.getPath() );
  }

  @Test
  public void testGetName() {
    String name = "name";
    when( path.getName() ).thenReturn( name );
    assertEquals( name, hadoopFileSystemPath.getName() );
  }

  @Test
  public void testToString() {
    assertEquals( path.toString(), hadoopFileSystemPath.toString() );
  }

  @Test
  public void testToUri() {
    assertEquals( uri, hadoopFileSystemPath.toUri() );
  }

  @Test
  public void testResolvePath() {
    assertEquals( "test/child",
      new HadoopFileSystemPathImpl( new Path( "test" ) ).resolve( new HadoopFileSystemPathImpl( new Path( "child" ) ) )
        .getPath() );
  }

  @Test
  public void testResolveString() {
    assertEquals( "test/child", new HadoopFileSystemPathImpl( new Path( "test" ) ).resolve( "child" ).getPath() );
  }

  @Test
  public void testGetParent() {
    Path parent = mock( Path.class );
    when( path.getParent() ).thenReturn( parent );
    assertEquals( parent, ( (HadoopFileSystemPathImpl) hadoopFileSystemPath.getParent() ).getRawPath() );
  }

  @Test
  public void testToHadoopFileSystemPathImpl() {
    HadoopFileSystemPathImpl hadoopFileSystemPath2 = new HadoopFileSystemPathImpl( path );
    assertTrue( hadoopFileSystemPath2 == HadoopFileSystemPathImpl.toHadoopFileSystemPathImpl( hadoopFileSystemPath2 ) );

    assertNull( HadoopFileSystemPathImpl.toHadoopFileSystemPathImpl( null ) );

    assertEquals( "/test", HadoopFileSystemPathImpl.toHadoopFileSystemPathImpl( hadoopFileSystemPath ).getPath() );

  }
}
