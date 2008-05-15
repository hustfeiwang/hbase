/**
 * Copyright 2007 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.filter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.SortedMap;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Implementation of RowFilterInterface that filters out rows greater than or 
 * equal to a specified rowKey.
 */
public class StopRowFilter implements RowFilterInterface {

  private byte [] stopRowKey;
  
  /**
   * Default constructor, filters nothing. Required though for RPC
   * deserialization.
   */
  public StopRowFilter() {
    super();
  }

  /**
   * Constructor that takes a stopRowKey on which to filter
   * 
   * @param stopRowKey rowKey to filter on.
   */
  public StopRowFilter(final byte [] stopRowKey) {
    this.stopRowKey = stopRowKey;
  }
  
  /**
   * An accessor for the stopRowKey
   * 
   * @return the filter's stopRowKey
   */
  public byte [] getStopRowKey() {
    return this.stopRowKey;
  }

  /**
   * 
   * {@inheritDoc}
   */
  public void validate(@SuppressWarnings("unused") final byte [][] columns) {
    // Doesn't filter columns
  }

  /**
   * 
   * {@inheritDoc}
   */
  public void reset() {
    // Nothing to reset
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unused")
  public void rowProcessed(boolean filtered, byte [] rowKey) {
    // Doesn't care
  }

  /** {@inheritDoc} */
  public boolean processAlways() {
    return false;
  }
  
  /** {@inheritDoc} */
  public boolean filterAllRemaining() {
    return false;
  }

  /** {@inheritDoc} */
  public boolean filterRowKey(final byte [] rowKey) {
    if (rowKey == null) {
      if (this.stopRowKey == null) {
        return true;
      }
      return false;
    }
    return Bytes.compareTo(stopRowKey, rowKey) <= 0;
  }

  /**
   * {@inheritDoc}
   *
   * Because StopRowFilter does not examine column information, this method 
   * defaults to calling the rowKey-only version of filter.
   */
  public boolean filterColumn(@SuppressWarnings("unused") final byte [] rowKey,
    @SuppressWarnings("unused") final byte [] colKey,
    @SuppressWarnings("unused") final byte[] data) {
    return filterRowKey(rowKey);
  }

  /** {@inheritDoc}
   *
   * Because StopRowFilter does not examine column information, this method 
   * defaults to calling filterAllRemaining().
   * 
   * @param columns
   */
  public boolean filterRow(@SuppressWarnings("unused")
      final SortedMap<byte [], byte[]> columns) {
    return filterAllRemaining();
  }

  /** {@inheritDoc} */
  public void readFields(DataInput in) throws IOException {
    this.stopRowKey = Bytes.readByteArray(in);
  }

  /** {@inheritDoc} */
  public void write(DataOutput out) throws IOException {
    Bytes.writeByteArray(out, this.stopRowKey);
  }
}