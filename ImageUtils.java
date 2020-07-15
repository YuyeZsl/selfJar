package com.yunjicn.yunjibaike.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtils {
	/**
	 * 获取ImageReader
	 * @param image
	 * @throws IOException
	 */
	public static ImageReader getImageReader(File image) throws IOException {
		ImageInputStream iis = ImageIO.createImageInputStream(image);
		Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
		if (!iter.hasNext()) {
			return null;
		}
		ImageReader reader = iter.next();
		iis.close();
		return reader;
	}
	/***
	 * 获取图片的类型
	 * @param image
	 * @return 图片的类型
	 * @throws IOException
	 */
	public static String getFormatName(File image) throws IOException {
		return getImageReader(image).getFormatName();
	}
	/**
	 * 将图片转为BufferedImage对象
	 * @param image
	 * @return BufferedImage
	 * @throws IOException
	 */
	public static BufferedImage getBufferedImage(File image) throws IOException {
		FileInputStream is = new FileInputStream(image);
		BufferedImage result = javax.imageio.ImageIO.read(is);
		is.close();
		return result;
	}
	/***
	 * 获取图片的宽
	 * @param image
	 * @return 图片的宽
	 * @throws IOException
	 */
	public static int getImageWidth(File image) throws IOException {
		return getImageReader(image).getWidth(0);
	}
	/***
	 * 获取图片的高
	 * @param image
	 * @return 图片的高
	 * @throws IOException
	 */
	public static int getImageHeight(File image) throws IOException {
		return getImageReader(image).getHeight(0);
	}
	/***
	 * 剪裁图片，得到裁剪后的BufferedImage对象
	 * @param image
	 * @param x 起点横坐标
	 * @param y 纵坐标
	 * @param w 裁剪长
	 * @param h 裁剪高
	 * @return 裁剪后的BufferedImage对象
	 * @throws IOException
	 */
	public static BufferedImage cutImage(File image, int x, int y, int w, int h) throws IOException {
		ImageReader reader = getImageReader(image);
		FileInputStream is = new FileInputStream(image);
		ImageInputStream iis = ImageIO.createImageInputStream(is);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		x = (x > 0 ? x : 0);
		y = (y > 0 ? y : 0);
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage result = reader.read(0, param);
		iis.close();
		is.close();
		return result;
	}
	/**
	 * 裁剪图片，并将裁剪后的图片写入新文件。
	 * @param srcImage 原图片
	 * @param destImage 新图片
	 * @param x 起点横坐标
	 * @param y 纵坐标
	 * @param w 裁剪长
	 * @param h 裁剪高
	 * @param formatName 图片类型。见{@link #getFormatName(File)}
	 * @throws IOException
	 */
	public static void cutImage(File srcImage, File destImage, int x, int y, int w, int h, String formatName) throws IOException {
		BufferedImage image = cutImage(srcImage,x,y,w,h);
		ImageIO.write(image, formatName, destImage);
	}
	/***
	 * 图片旋转指定角度,得到旋转后的BufferedImage对象
	 * @param image 图像
	 * @param degree 角度
	 * @return 旋转后的BufferedImage对象
	 * @throws IOException
	 */
	public static BufferedImage rotateImage(File image, int degree) throws IOException {
		BufferedImage bufferedimage = getBufferedImage(image);
		int w = bufferedimage.getWidth();
		int h = bufferedimage.getHeight();
		int type = bufferedimage.getColorModel().getTransparency();
		BufferedImage img;
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type)).createGraphics()).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2d.setPaint(Color.WHITE);
		graphics2d.fillRect(0, 0, w, h);
		graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
		graphics2d.drawImage(bufferedimage, 0, 0, Color.WHITE, null);
		graphics2d.dispose();
		return img;
	}
	/***
	 * 图片旋转指定角度，并将旋转后的图片写入新文件。
	 * @param srcImage 原图像
	 * @param destImage 新图像
	 * @param degree 角度
	 * @param formatName 图片类型。见{@link #getFormatName(File)}
	 * @throws IOException
	 */
	public static void rotateImage(File srcImage, File destImage, int degree, String formatName) throws IOException {
		BufferedImage tag = rotateImage(srcImage, degree);
		ImageIO.write(tag, formatName, destImage);
	}
	/**
	 * 旋转图片，得到旋转后的BufferedImage对象
	 * @param image 图像
	 * @param angel 角度
	 * @return 旋转后的BufferedImage对象
	 * @throws IOException
	 */
	public static BufferedImage rotate(File image, int angel) throws IOException {
		BufferedImage src = getBufferedImage(image);
		int src_width = src.getWidth(null);
		int src_height = src.getHeight(null);
		Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);
		BufferedImage res = null;
		res = new BufferedImage(rect_des.width, rect_des.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = res.createGraphics();
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, rect_des.width, rect_des.height);
		g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
		g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
		g2.drawImage(src, null, null);
		return res;
	}
	/**
	 * 计算旋转后的大小
	 * @param src 原矩形
	 * @param angel 旋转角度
	 * @return 新矩形
	 * @throws IOException
	 */
	public static Rectangle calcRotatedSize(Rectangle src, int angel) {
		if (angel >= 90) {
			if (angel / 90 % 2 == 1) {
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}
		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new java.awt.Rectangle(new Dimension(des_width, des_height));
	}
	/**
	 * 图片旋转指定角度，并将旋转后的图片写入新文件。
	 * @param srcImage 原图像
	 * @param destImage 新图像
	 * @param angel 角度
	 * @param formatName 图片类型。见{@link #getFormatName(File)}
	 * @throws IOException
	 */
	public static void rotate(File srcImage, File destImage, int angel, String formatName) throws IOException {
		angel = angel > 0 ? angel : (360 - angel);
		BufferedImage tag = rotate(srcImage, angel);
		ImageIO.write(tag, formatName, destImage);
	}
	/**
	 * 先旋转再切割图片，并将新图像保存
	 * @param srcImage 源图像
	 * @param destImage 新图像
	 * @param x 起点横坐标
	 * @param y 纵坐标
	 * @param w 裁剪长
	 * @param h 裁剪高
	 * @param angel 旋转角度
	 * @throws IOException
	 */
	public static void rotateAndCutImage(File srcImage, File destImage, int x, int y, int w, int h, int angel) throws IOException {
		String formatName = getFormatName(srcImage);
		File dest1 = new File(destImage.getParentFile(), destImage.getName() + ".1");
		rotate(srcImage, dest1, angel,formatName);
		cutImage(dest1, destImage, x, y, w, h, formatName);
		dest1.delete();
	}
	/***
	 * 按照比例缩放，并将新图像保存
	 * @param srcImage 原图像
	 * @param destImage 新图像
	 * @param scale 缩放比例
	 * @throws IOException
	 */
	public static void scale(File srcImage, File destImage, int scale) throws IOException {
		BufferedImage src = getBufferedImage(srcImage);
		int width = src.getWidth(); // 得到源图宽
		int height = src.getHeight(); // 得到源图长
		width = width * scale;
		height = height * scale;
		Image image = src.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = tag.getGraphics();
		g.drawImage(image, 0, 0, null); // 绘制缩小后的图
		g.dispose();
		ImageIO.write(tag, getFormatName(srcImage), destImage);
	}
	/***
	 * 缩放图像，按照长宽缩放，并将新图像保存
	 * @param srcImage 原图像
	 * @param destImage 新图像
	 * @param height 变换后的高度
	 * @param width 变换后的长度
	 * @param bb 比例不对称时，是否补白，true 补白;false 不补白
	 * @throws IOException
	 */
	public static void scale(File srcImage, File destImage, int height, int width, boolean bb) throws IOException {
		double ratio = 0.0; // 缩放比例
		BufferedImage bi = getBufferedImage(srcImage);
		int width1 = bi.getWidth(); // 得到源图宽
		int height1 = bi.getHeight(); // 得到源图长
		Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
		BufferedImage result = null;
		// 计算比例
		if ((height1 != height) || (width1 != width)) {
			if (height1 > width1) {
				ratio = (new Integer(height)).doubleValue() / height1;
			} else {
				ratio = (new Integer(width)).doubleValue() / width1;
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			result = op.filter(bi, null);
		}
		if (bb) {// 补白
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			if (width == itemp.getWidth(null))
				g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
			else
				g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
			g.dispose();
			result = image;
		}
		if(result == null){
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = image.createGraphics();
			g.drawImage(itemp, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			result = image;
		}
		ImageIO.write(result, getFormatName(srcImage), destImage);
	}
	/**
	 * 先旋转、再切割、再根据比例缩放图片，并将新图像保存
	 * @param srcImage 源图像
	 * @param destImage 新图像
	 * @param x 起点横坐标
	 * @param y 纵坐标
	 * @param w 裁剪长
	 * @param h 裁剪高
	 * @param angel 旋转角度
	 * @param scale 缩放比例
	 * @throws IOException
	 */
	public static void rotateCutAndScaleImage(File srcImage, File destImage, int x, int y, int w, int h, int angel, int scale) throws IOException {
		String formatName = getFormatName(srcImage);	
		File dest1 = new File(destImage.getParentFile(), destImage.getName() + ".1");
		rotate(srcImage, dest1, angel,formatName);
		File dest2 = new File(destImage.getParentFile(), destImage.getName() + ".2");
		cutImage(dest1, dest2, x, y, w, h, formatName);
		scale(dest2, destImage, scale);
		dest1.delete();
		dest2.delete();
	}
	/**
	 * 先旋转、再切割、再根据像素缩放图片，并将新图像保存
	 * @param srcImage 源图像
	 * @param destImage 新图像
	 * @param x 起点横坐标
	 * @param y 纵坐标
	 * @param w 裁剪长
	 * @param h 裁剪高
	 * @param angel 旋转角度
	 * @param height 变换后的高度
	 * @param width 变换后的长度
	 * @param bb 比例不对称时，是否补白，true 补白;false 不补白 
	 * @throws IOException
	 */
	public static void rotateCutAndScaleImage(File srcImage, File destImage, int x, int y, int w, int h, int angel, int height, int width, boolean bb) throws IOException {
		String formatName = getFormatName(srcImage);
		File dest1 = new File(destImage.getParentFile(), destImage.getName() + ".1");
		rotate(srcImage, dest1, angel,formatName);
		File dest2 = new File(destImage.getParentFile(), destImage.getName() + ".2");
		cutImage(dest1, dest2, x, y, w, h, formatName);
		scale(dest2, destImage, height, width, bb);
		dest1.delete();
		dest2.delete();
	}
	/***
	 * 转换图像格式，并将新图像保存
	 * @param srcImage 源图像
	 * @param destImage 新图像
	 * @param formatName 转换类型
	 * @throws IOException
	 */
	public static void convert(File srcImage, File destImage, String formatName) throws IOException {
		BufferedImage src = ImageIO.read(srcImage);
		ImageIO.write(src, formatName, destImage);
	}
	/**
	 * 彩色转为黑白，并将新图像保存
	 * @param srcImage 源图像
	 * @param destImage 新图像
	 * @throws IOException
	 */
	public final static void gray(File srcImage, File destImage) throws IOException {
		BufferedImage src = ImageIO.read(srcImage);
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		src = op.filter(src, null);
		ImageIO.write(src, getFormatName(srcImage), destImage);
	}

	/**
	 * 给图片添加图片水印，并将新图像保存
	 * @param srcImage 源图像
	 * @param destImage 新图像
	 * @param pressImg 水印图片
	 * @param x 修正值。 默认在中间
	 * @param y 修正值。 默认在中间
	 * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 * @throws IOException
	 */
	public static void pressImage(File srcImage, File destImage, File pressImg, int x, int y, float alpha) throws IOException {
		BufferedImage src = ImageIO.read(srcImage); // 读入文件
		int width = src.getWidth(); // 得到源图宽
		int height = src.getHeight(); // 得到源图长
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(src, 0, 0, width, height, null);
		// 水印文件
		Image src_biao = ImageIO.read(pressImg);
		int wideth_biao = src_biao.getWidth(null);
		int height_biao = src_biao.getHeight(null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		g.drawImage(src_biao, (width - wideth_biao - x), (height - height_biao - y), wideth_biao, height_biao, null);
		// 水印文件结束
		g.dispose();
		ImageIO.write(image, getFormatName(srcImage), destImage);
	}
	/**
	 * 给图片添加文字水印，并将新图像保存
	 * @param srcImage 源图像
	 * @param destImage 新图像
	 * @param pressText 水印文字
	 * @param fontName 水印的字体名称
	 * @param fontStyle 水印的字体样式
	 * @param color 水印的字体颜色
	 * @param fontSize 水印的字体大小
	 * @param x 修正值
	 * @param y 修正值
	 * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 * @throws IOException
	 */
	public static void pressText(File srcImage, File destImage, String pressText, String fontName, int fontStyle, Color color, int fontSize, int x, int y, float alpha) throws IOException {
		BufferedImage src = ImageIO.read(srcImage); // 读入文件
		int width = src.getWidth(); // 得到源图宽
		int height = src.getHeight(); // 得到源图长
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(src, 0, 0, width, height, null);
		g.setColor(color);
		g.setFont(new Font(fontName, fontStyle, fontSize));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
		// 在指定坐标绘制水印文字
		g.drawString(pressText, (width - (getLength(pressText) * fontSize)) / 2 + x, (height - fontSize) / 2 + y);
		g.dispose();
		ImageIO.write(image, getFormatName(srcImage), destImage);
	}
	/**
	 * 计算text的长度（一个中文算两个字符）
	 * @param text
	 * @return
	 */
	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}
}