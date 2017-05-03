/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.angel.psagent.matrix;

import com.tencent.angel.exception.AngelException;
import com.tencent.angel.ml.math.TMatrix;
import com.tencent.angel.ml.math.TVector;
import com.tencent.angel.ml.matrix.udf.aggr.AggrFunc;
import com.tencent.angel.ml.matrix.udf.aggr.AggrResult;
import com.tencent.angel.ml.matrix.udf.getrow.GetRowFunc;
import com.tencent.angel.ml.matrix.udf.getrow.GetRowResult;
import com.tencent.angel.ml.matrix.udf.updater.UpdaterFunc;
import com.tencent.angel.ml.matrix.udf.updater.VoidResult;
import com.tencent.angel.psagent.matrix.transport.adapter.GetRowsResult;
import com.tencent.angel.psagent.matrix.transport.adapter.RowIndex;

import java.util.concurrent.Future;

/**
 * Matrix operation interface used by ps client. In addition to the basic operations, we also define
 * the POFs(Ps Oriented Function), which can be used to extend the functionality of the ps.
 */
public interface MatrixInterface {
  public void zero() throws AngelException;

  public void increment(double[] delta) throws AngelException;

  public void increment(int[] indexes, double[] delta) throws AngelException;

  public double[] get(int[] indexes) throws AngelException;

  public double[] get() throws AngelException;
   
  /**
   * Use the aggregate function defined by user to get the aggregate value of the matrix. 
   * 
   * @param func the function used to calculate the aggregate value
   * @return AggrResult the aggregate value of the matrix
   * @throws AngelException
   */
  public AggrResult aggr(AggrFunc func) throws AngelException;
  
  /**
   * Use the update function defined by user to update the matrix.
   * 
   * @param func the function used to update the matrix
   * @return Future<VoidResult> the result future, user can choose whether to wait for the update result
   * @throws AngelException
   */
  public Future<VoidResult> update(UpdaterFunc func) throws AngelException;
  
  /**
   * Use the get function defined by user to get a matrix row.
   * 
   * @param func the function used to get a row of the matrix
   * @return GetRowResult 
   * @throws AngelException
   */
  public GetRowResult getRow(GetRowFunc func) throws AngelException;

  /**
   * Use a update vector which has same dimension with matrix row to increment the matrix row.
   * 
   * @param rowIndex row index
   * @param delta the update vector
   * @throws AngelException
   */
  public void increment(int rowIndex, TVector delta) throws AngelException;

  /**
   * Use a update vector which has same dimension with matrix row to increment the matrix row.
   * 
   * @param delta the update vector
   * @throws AngelException
   */
  public void increment(TVector delta) throws AngelException;

  /**
   * Use a update matrix which has same dimension with the matrix to increment the matrix.
   * 
   * @param delta the update matrix
   * @throws AngelException
   */
  public void increment(TMatrix delta) throws AngelException;

  /**
   * Get a matrix row.
   * 
   * @param rowIndex row index
   * @return TVector matrix row
   * @throws AngelException
   */
  public TVector getRow(int rowIndex) throws AngelException;

  /**
   * Get a batch of rows use the pipeline mode. The pipeline mode means that user can calculate part
   * of rows while fetch others.
   * 
   * @param index row indexes
   * @param batchSize the batch size return to user once
   * @return GetRowsResult the result which contains a blocking queue
   * @throws AngelException
   */
  public GetRowsResult getRowsFlow(RowIndex index, int batchSize) throws AngelException;
  
  /**
   * Update the clock value of the matrix.
   * 
   * @param flushFirst true means flush matrix oplogs in cache to ps first 
   * @return Future<VoidResult> the result future, user can choose whether to wait for the operation result
   * @throws AngelException
   */
  public Future<VoidResult> clock(boolean flushFirst) throws AngelException; 
  
  /**
   * Flush matrix oplogs in cache to ps.
   * 
   * @return Future<VoidResult> the result future, user can choose whether to wait for the operation result
   * @throws AngelException
   */
  public Future<VoidResult> flush() throws AngelException;

  /**
   * Update the clock value of the matrix.
   * 
   * @return Future<VoidResult> the result future, user can choose whether to wait for the operation result
   * @throws AngelException
   */
  public Future<VoidResult> clock() throws AngelException;
}
