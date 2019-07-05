package com.fanglin.common.utils;

import com.fanglin.common.core.others.BusinessException;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 * @author 彭方林
 * @date 2019/4/2 17:56
 * @version 1.0
 **/
@Slf4j
public class QrCodeUtils {
	/**
	 * 生成二维码
	 */
	public static void createQrCode(String filePath, String content) {
		//文件夹路径
		File dirPath = new File(filePath.substring(0, filePath.lastIndexOf("/")));
		if (!dirPath.exists()) {
			boolean success = dirPath.mkdirs();
			if (!success) {
				log.warn("创建目录失败 {}",dirPath.getPath());
				throw new BusinessException("创建目录失败");
			}
		}
		int width = 200;
		int height = 200;
		Map<EncodeHintType, Object> hints = new HashMap<>(5);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			// 生成矩阵
			BitMatrix bitMatrix= new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			Path path = FileSystems.getDefault().getPath(filePath);
			// 输出图像
			MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
		} catch (WriterException | IOException e) {
			log.warn("二维码生成失败:{}",e.getMessage());
			throw new BusinessException("二维码生成失败");
		}
	}

	/**
	 * 解析二维码数据
	 */
	public static String readQrCode(String filePath) {
		BufferedImage image;
		try {
			image = ImageIO.read(new File(filePath));
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Map<DecodeHintType, Object> hints = new HashMap<>(5);
			hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
			// 对图像进行解码
			Result result = new MultiFormatReader().decode(binaryBitmap, hints);
			return result.getText();
		} catch (IOException | NotFoundException e) {
			log.warn(e.getMessage());
			throw new BusinessException("文件不存在");
		}
	}
}
