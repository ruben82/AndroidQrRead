/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.application.food.zxing.multi.qrcode;

import com.application.food.zxing.BarcodeFormat;
import com.application.food.zxing.BinaryBitmap;
import com.application.food.zxing.DecodeHintType;
import com.application.food.zxing.NotFoundException;
import com.application.food.zxing.ReaderException;
import com.application.food.zxing.Result;
import com.application.food.zxing.ResultMetadataType;
import com.application.food.zxing.ResultPoint;
import com.application.food.zxing.common.DecoderResult;
import com.application.food.zxing.common.DetectorResult;
import com.application.food.zxing.multi.MultipleBarcodeReader;
import com.application.food.zxing.multi.qrcode.detector.MultiDetector;
import com.application.food.zxing.qrcode.QRCodeReader;
import com.application.food.zxing.BarcodeFormat;
import com.application.food.zxing.BinaryBitmap;
import com.application.food.zxing.DecodeHintType;
import com.application.food.zxing.NotFoundException;
import com.application.food.zxing.ReaderException;
import com.application.food.zxing.Result;
import com.application.food.zxing.ResultMetadataType;
import com.application.food.zxing.ResultPoint;
import com.application.food.zxing.common.DecoderResult;
import com.application.food.zxing.common.DetectorResult;
import com.application.food.zxing.multi.MultipleBarcodeReader;
import com.application.food.zxing.multi.qrcode.detector.MultiDetector;
import com.application.food.zxing.qrcode.QRCodeReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This implementation can detect and decode multiple QR Codes in an image.
 *
 * @author Sean Owen
 * @author Hannes Erven
 */
public final class QRCodeMultiReader extends QRCodeReader implements MultipleBarcodeReader {

  private static final Result[] EMPTY_RESULT_ARRAY = new Result[0];

  @Override
  public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
    return decodeMultiple(image, null);
  }

  @Override
  public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType,?> hints) throws NotFoundException {
    List<Result> results = new ArrayList<Result>();
    DetectorResult[] detectorResults = new MultiDetector(image.getBlackMatrix()).detectMulti(hints);
    for (DetectorResult detectorResult : detectorResults) {
      try {
        DecoderResult decoderResult = getDecoder().decode(detectorResult.getBits());
        ResultPoint[] points = detectorResult.getPoints();
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points,
                                   BarcodeFormat.QR_CODE);
        List<byte[]> byteSegments = decoderResult.getByteSegments();
        if (byteSegments != null) {
          result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
        }
        String ecLevel = decoderResult.getECLevel();
        if (ecLevel != null) {
          result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
        }
        results.add(result);
      } catch (ReaderException re) {
        // ignore and continue 
      }
    }
    if (results.isEmpty()) {
      return EMPTY_RESULT_ARRAY;
    } else {
      return results.toArray(new Result[results.size()]);
    }
  }

}