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


package com.pentaho.big.data.bundles.impl.shim.hbase.connectionPool;

import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.hadoop.shim.api.cluster.NamedCluster;
import org.pentaho.hadoop.shim.spi.HBaseConnection;
import org.pentaho.hadoop.shim.spi.HBaseShim;
import org.pentaho.hbase.shim.common.CommonHBaseConnection;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by bryan on 1/25/16.
 */
public class HBaseConnectionPool implements Closeable {
  private final Set<HBaseConnectionPoolConnection> availableConnections;
  private final Set<HBaseConnectionPoolConnection> inUseConnections;
  private final HBaseShim hBaseShim;
  protected final Properties connectionProps;
  protected final LogChannelInterface logChannelInterface;
  protected final NamedCluster namedCluster;

  public HBaseConnectionPool( HBaseShim hBaseShim, Properties connectionProps,
                              LogChannelInterface logChannelInterface, NamedCluster namedCluster ) {
    this.hBaseShim = hBaseShim;
    this.connectionProps = connectionProps;
    this.logChannelInterface = logChannelInterface;
    availableConnections = new HashSet<>();
    inUseConnections = new HashSet<>();
    this.namedCluster = namedCluster;
  }

  private HBaseConnectionPoolConnection findBestMatch( String sourceTable ) {
    HBaseConnectionPoolConnection match = null;
    for ( HBaseConnectionPoolConnection availableConnection : availableConnections ) {
      String availableConnectionSourceTable = availableConnection.getSourceTable();
      if ( sourceTable == null ) {
        if ( availableConnectionSourceTable == null ) {
          return availableConnection;
        }
      } else {
        if ( availableConnectionSourceTable == null ) {
          match = availableConnection;
        } else if ( sourceTable.equals( availableConnectionSourceTable ) ) {
          return availableConnection;
        }
      }
    }
    if ( match == null && availableConnections.size() > 0 ) {
      return availableConnections.iterator().next();
    } else {
      return match;
    }
  }

  private HBaseConnectionPoolConnection findBestMatch( String targetTable, Properties targetTableProps ) {
    HBaseConnectionPoolConnection match = null;
    for ( HBaseConnectionPoolConnection availableConnection : availableConnections ) {
      String availableConnectionTargetTable = availableConnection.getTargetTable();
      if ( targetTable == null ) {
        if ( availableConnectionTargetTable == null ) {
          return availableConnection;
        }
      } else {
        if ( availableConnectionTargetTable == null ) {
          match = availableConnection;
        } else if ( targetTable.equals( availableConnectionTargetTable ) ) {
          Properties availableConnectionTargetTableProperties = availableConnection.getTargetTableProperties();
          if ( targetTableProps == null ) {
            if ( availableConnectionTargetTableProperties == null ) {
              return availableConnection;
            }
          } else if ( targetTableProps.equals( availableConnectionTargetTableProperties ) ) {
            return availableConnection;
          }
        }
      }
    }
    if ( match == null && availableConnections.size() > 0 ) {
      return availableConnections.iterator().next();
    } else {
      return match;
    }
  }

  private HBaseConnectionPoolConnection findBestMatch() {
    HBaseConnectionPoolConnection match = null;
    for ( HBaseConnectionPoolConnection availableConnection : availableConnections ) {
      String sourceTable = availableConnection.getSourceTable();
      String targetTable = availableConnection.getTargetTable();
      if ( targetTable == null ) {
        if ( sourceTable == null ) {
          return availableConnection;
        }
        match = availableConnection;
      } else if ( sourceTable == null && match == null ) {
        match = availableConnection;
      }
    }
    if ( match == null && availableConnections.size() > 0 ) {
      return availableConnections.iterator().next();
    } else {
      return match;
    }
  }

  protected HBaseConnectionPoolConnection create() throws IOException {
    HBaseConnection hBaseConnection =
      ( hBaseShim != null ? hBaseShim.getHBaseConnection() : new CommonHBaseConnection() );
    try {
      List<String> messages = new ArrayList<>();
      hBaseConnection.configureConnection( connectionProps, namedCluster, messages );
      if ( logChannelInterface != null ) {
        for ( String message : messages ) {
          logChannelInterface.logBasic( message );
        }
      }
    } catch ( Exception e ) {
      throw new IOException( e );
    }
    return new HBaseConnectionPoolConnection( hBaseConnection );
  }

  /**
   * Gets an available connection with the given source table (changing to this source table if necessary)
   * <p/>
   * Tries to get one that already has that source table, then prefers one without a source table, then one with a
   * different source table, then creates a new table
   *
   * @param sourceTable
   * @return
   * @throws IOException
   */
  public synchronized HBaseConnectionHandle getConnectionHandle( String sourceTable ) throws IOException {
    HBaseConnectionPoolConnection result = findBestMatch( sourceTable );
    if ( result != null ) {
      availableConnections.remove( result );
    } else {
      result = create();
    }
    if ( sourceTable != null && !sourceTable.equals( result.getSourceTable() ) ) {
      try {
        result.newSourceTableInternal( sourceTable );
      } catch ( Exception e ) {
        throw new IOException( e );
      }
    }
    inUseConnections.add( result );
    return new HBaseConnectionHandleImpl( this, result );
  }

  /**
   * Gets a connection with the given target table and properties
   * <p/>
   * Tries to get one with the correct target table and properties, then prefers one without a target table, then one
   * with a different target table and properties
   *
   * @param targetTable
   * @param targetTableProps
   * @return
   * @throws IOException
   */
  public synchronized HBaseConnectionHandle getConnectionHandle( String targetTable, Properties targetTableProps )
    throws IOException {
    HBaseConnectionPoolConnection result = findBestMatch( targetTable, targetTableProps );
    if ( result != null ) {
      availableConnections.remove( result );
    } else {
      result = create();
    }
    boolean targetTableDifferent = targetTable != null && !targetTable.equals( result.getTargetTable() );
    boolean propsDifferent = false;
    Properties resultTargetTableProperties = result.getTargetTableProperties();
    if ( targetTableProps == null ) {
      propsDifferent = resultTargetTableProperties != null;
    } else {
      propsDifferent = targetTableProps.equals( resultTargetTableProperties );
    }
    if ( targetTableDifferent || propsDifferent ) {
      try {
        result.newTargetTableInternal( targetTable, targetTableProps );
      } catch ( Exception e ) {
        throw new IOException( e );
      }
    }
    inUseConnections.add( result );
    return new HBaseConnectionHandleImpl( this, result );
  }

  /**
   * Gets a connection with no concern for source or target table
   * <p/>
   * Prefers one without source or target, then on with source, then one with target
   *
   * @return
   * @throws IOException
   */
  public synchronized HBaseConnectionHandle getConnectionHandle() throws IOException {
    HBaseConnectionPoolConnection result = findBestMatch();
    if ( result != null ) {
      availableConnections.remove( result );
    } else {
      result = create();
    }
    inUseConnections.add( result );
    return new HBaseConnectionHandleImpl( this, result );
  }

  protected synchronized void releaseConnection( HBaseConnectionPoolConnection hBaseConnection ) {
    inUseConnections.remove( hBaseConnection );
    availableConnections.add( hBaseConnection );
  }

  @Override public synchronized void close() throws IOException {
    for ( HBaseConnectionPoolConnection inUseConnection : inUseConnections ) {
      try {
        inUseConnection.closeInternal();
      } catch ( Exception e ) {
        logChannelInterface.logError( e.getMessage(), e );
      }
    }
    for ( HBaseConnectionPoolConnection availableConnection : availableConnections ) {
      try {
        availableConnection.closeInternal();
      } catch ( Exception e ) {
        logChannelInterface.logError( e.getMessage(), e );
      }
    }
    inUseConnections.clear();
    availableConnections.clear();
  }
}
