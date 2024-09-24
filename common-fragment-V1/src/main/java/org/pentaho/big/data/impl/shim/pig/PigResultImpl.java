/*******************************************************************************
 *
 * Pentaho Big Data
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.big.data.impl.shim.pig;

import org.apache.commons.vfs2.FileObject;
import org.pentaho.hadoop.shim.api.pig.PigResult;

/**
 * Created by bryan on 7/9/15.
 */
public class PigResultImpl implements PigResult {
  private final FileObject logFile;
  private final int[] result;
  private final Exception exception;

  public PigResultImpl( FileObject logFile, int[] result, Exception exception ) {
    this.logFile = logFile;
    this.result = result;
    this.exception = exception;
  }

  @Override public FileObject getLogFile() {
    return logFile;
  }

  @Override public int[] getResult() {
    return result;
  }

  @Override public Exception getException() {
    return exception;
  }
}
