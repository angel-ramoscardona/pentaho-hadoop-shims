/*******************************************************************************
 *
 * Pentaho Big Data
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.hbase.shim.common;

import org.pentaho.hadoop.shim.ShimVersion;
import org.pentaho.hbase.shim.spi.HBaseConnection;
import org.pentaho.hbase.shim.spi.HBaseShim;

/**
 * Concrete implementation of HBaseShim suitable for use with Apache HBase 0.90.x.
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public class CommonHBaseShim implements HBaseShim {

  @Override
  public ShimVersion getVersion() {
    return new ShimVersion( 1, 0 );
  }

  public HBaseConnection getHBaseConnection() {
    return new CommonHBaseConnection();
  }
}