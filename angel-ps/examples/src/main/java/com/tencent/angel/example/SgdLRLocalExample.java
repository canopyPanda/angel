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
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.tencent.angel.example;

import com.tencent.angel.conf.AngelConfiguration;
import com.tencent.angel.ml.algorithm.classification.lr.LRRunner;
import com.tencent.angel.ml.algorithm.conf.MLConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.log4j.PropertyConfigurator;

import java.util.Scanner;

public class SgdLRLocalExample {
  private Configuration conf = new Configuration();

  static {
    PropertyConfigurator
        .configure("/Users/mac/Documents/newdevelop-1.1.8/angel/angel-ps/log4j.properties");
  }

  public void setConf() {

    // Feature number of train data
    int featureNum = 124;
    // Total iteration number
    int epochNum = 2;
    // Validation sample Ratio
    double vRatio = 0.1;
    // Data format, libsvm or dummy
    String dataFmt = "libsvm";
    // Train batch number per epoch.
    int batchNum = 10;

    // Learning rate
    double learnRate = 1;
    // Decay of learning rate
    double decay = 0.1;
    // Regularization coefficient
    double reg = 0;

    // Set local deploy mode
    conf.set(AngelConfiguration.ANGEL_DEPLOY_MODE, "LOCAL");

    // Set basic configuration keys
    conf.setBoolean("mapred.mapper.new-api", true);
    conf.set(AngelConfiguration.ANGEL_INPUTFORMAT_CLASS, CombineTextInputFormat.class.getName());
    conf.setBoolean(AngelConfiguration.ANGEL_JOB_OUTPUT_PATH_DELETEONEXIST, true);

    // Set data format
    conf.set(MLConf.ML_DATAFORMAT(), dataFmt);

    // set angel resource parameters #worker, #task, #PS
    conf.setInt(AngelConfiguration.ANGEL_WORKERGROUP_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_WORKER_TASK_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_PS_NUMBER, 1);

    // set sgd LR algorithm parameters #feature #epoch
    conf.set(MLConf.ML_FEATURE_NUM(), String.valueOf(featureNum));
    conf.set(MLConf.ML_EPOCH_NUM(), String.valueOf(epochNum));
    conf.set(MLConf.ML_BATCH_NUM(), String.valueOf(batchNum));
    conf.set(MLConf.ML_VALIDATE_RATIO(), String.valueOf(vRatio));
    conf.set(MLConf.ML_LEAR_RATE(), String.valueOf(learnRate));
    conf.set(MLConf.ML_LEARN_DECAY(), String.valueOf(decay));
    conf.set(MLConf.ML_REG_L2(), String.valueOf(reg));
  }

  public void trainOnLocalCluster() throws Exception {
    setConf();
    String inputPath =
        "/Users/mac/Documents/newdevelop-1.1.8/angel/angel-ps/mllib/src/test/data/lr/a9a.train";
    String LOCAL_FS = LocalFileSystem.DEFAULT_FS;
    String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

    // Set trainning data path
    conf.set(AngelConfiguration.ANGEL_TRAIN_DATA_PATH, inputPath);
    // Set save model path
    conf.set(AngelConfiguration.ANGEL_SAVE_MODEL_PATH, LOCAL_FS + TMP_PATH + "/model");
    // Set actionType train
    conf.set(MLConf.ANGEL_ACTION_TYPE(), MLConf.ANGEL_ML_TRAIN());

    LRRunner runner = new LRRunner();
    runner.train(conf);
  }



  public void incTrain() {
    setConf();
    String inputPath =
        "/Users/mac/Documents/newdevelop-1.1.8/angel/angel-ps/mllib/src/test/data/lr/a9a.train";
    String LOCAL_FS = LocalFileSystem.DEFAULT_FS;
    String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

    // Set trainning data path
    conf.set(AngelConfiguration.ANGEL_TRAIN_DATA_PATH, inputPath);
    // Set load model path
    conf.set(AngelConfiguration.ANGEL_LOAD_MODEL_PATH, LOCAL_FS + TMP_PATH + "/model");
    // Set save model path
    conf.set(AngelConfiguration.ANGEL_SAVE_MODEL_PATH, LOCAL_FS + TMP_PATH + "/newmodel");
    // Set actionType incremental train
    conf.set(MLConf.ANGEL_ACTION_TYPE(), MLConf.ANGEL_ML_INC_TRAIN());

    LRRunner runner = new LRRunner();
    runner.incTrain(conf);
  }


  public void predict() {
    setConf();
    String inputPath =
        "/Users/mac/Documents/newdevelop-1.1.8/angel/angel-ps/mllib/src/test/data/lr/a9a.test";
    String LOCAL_FS = LocalFileSystem.DEFAULT_FS;
    String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

    // Set trainning data path
    conf.set(AngelConfiguration.ANGEL_TRAIN_DATA_PATH, inputPath);
    // Set load model path
    conf.set(AngelConfiguration.ANGEL_LOAD_MODEL_PATH, LOCAL_FS + TMP_PATH + "/model");
    // Set predict result path
    conf.set(AngelConfiguration.ANGEL_PREDICT_PATH, LOCAL_FS + TMP_PATH + "/predict");
    // Set actionType prediction
    conf.set(MLConf.ANGEL_ACTION_TYPE(), MLConf.ANGEL_ML_INC_TRAIN());

    conf.set(MLConf.ANGEL_ACTION_TYPE(), MLConf.ANGEL_ML_PREDICT());
    LRRunner runner = new LRRunner();

    runner.predict(conf);
  }

  public static void main(String[] args) throws Exception {
    SgdLRLocalExample example = new SgdLRLocalExample();
    Scanner scanner = new Scanner(System.in);
    System.out.println("1-trainOnLocalCluster 2-incTrain 3-predict");
    System.out.println("Please input the mode:");
    int mode = scanner.nextInt();
    switch (mode) {
      case 1:
        example.trainOnLocalCluster();
        break;
      case 2:
        example.incTrain();
        break;
      case 3:
        example.predict();
        break;
    }
  }
}
