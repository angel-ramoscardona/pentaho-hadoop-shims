/*******************************************************************************
 *
 * Pentaho Big Data
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/


package org.pentaho.hadoop.shim.pvfs.conf;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.connections.ConnectionDetails;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class S3ConfTest {

  private S3Conf s3Conf;
  private S3Conf badS3Conf;
  private Path path;
  @Mock private ConnectionDetails hcpConn;
  @Mock private ConnectionDetails s3Conn;

  @Before public void before() {
    Map<String, String> props = new HashMap<>();
    props.put( "accessKey", "ACCESSKEY" );
    props.put( "secretKey", "SECRETKEY" );
    props.put( "sessionToken", ":LKJL:KJJL" );
    props.put( "credentialsFilePath", "~/.aws/credentials" );
    props.put( "region", "us-west-1" );
    props.put( "profileName", "default" );

    when( hcpConn.getType() ).thenReturn( "hcp" );
    when( s3Conn.getProperties() ).thenReturn( props );
    when( s3Conn.getType() ).thenReturn( "s3" );
    s3Conf = new S3Conf( s3Conn );
    badS3Conf = new S3Conf( hcpConn );
    path = new Path( "pvfs://namedConn/bucket/somedir/somechild" );
  }

  @Test public void testSupportedSchemes() {
    assertTrue( s3Conf.supportsConnection() );
    assertFalse( badS3Conf.supportsConnection() );
  }

  @Test public void mapPath() {
    Path result = s3Conf.mapPath( path );
    assertThat( result.toString(), equalTo( "s3a://bucket/somedir/somechild" ) );
  }

  @Test public void testConf() {
    Configuration conf = s3Conf.conf( path );
    assertThat( conf.get( "fs.s3a.aws.credentials.provider" ),
      equalTo( "org.apache.hadoop.fs.s3a.TemporaryAWSCredentialsProvider" ) );
    assertThat( conf.get( "fs.s3a.access.key" ), equalTo( "ACCESSKEY" ) );
    assertThat( conf.get( "fs.s3a.secret.key" ), equalTo( "SECRETKEY" ) );
  }
}
