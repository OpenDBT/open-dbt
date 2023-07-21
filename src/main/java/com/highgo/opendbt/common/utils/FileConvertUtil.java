
package com.highgo.opendbt.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xslf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;


/**
 * <p>
 * 文件转换工具类$
 * </p>
 *
 * @author
 * @description
 * @date 2020/11/23$ 16:19$
 */

@Slf4j
public class FileConvertUtil {


  /**
   * `word` 转 `pdf`
   *
   * @param wordBytes: word字节码
   * @return 生成的`pdf`字节码
   * @author
   * @date 2020/11/26 13:39
   */

  public static byte[] wordBytes2PdfBytes(byte[] wordBytes) {
    MatchLicense.init();
    return Word2PdfUtil.wordBytes2PdfBytes(wordBytes);
  }


  /**
   * `word` 转 `pdf`
   *
   * @param wordBytes:   word字节码
   * @param pdfFilePath: 需转换的`pdf`文件路径
   * @return 生成的`pdf`文件数据
   * @author
   * @date 2020/11/26 13:39
   */

  public static File wordBytes2PdfFile(byte[] wordBytes, String pdfFilePath) {
    MatchLicense.init();
    return Word2PdfUtil.wordBytes2PdfFile(wordBytes, pdfFilePath);
  }


  /**
   * `excel` 转 `pdf`
   *
   * @param excelBytes: html字节码
   * @return 生成的`pdf`文件流
   * @author zhengqing
   * @date 2020/11/24 11:26
   */

  public static byte[] excelBytes2PdfBytes(byte[] excelBytes) {
    MatchCellLicense.init();
    return Excel2PdfUtil.excelBytes2PdfBytes(excelBytes);
  }


  /**
   * `excel` 转 `pdf`
   *
   * @param excelBytes:  excel文件字节码
   * @param pdfFilePath: 待生成的`pdf`文件路径
   * @return 生成的`Pdf`文件数据
   * @author zhengqing
   * @date 2020/11/24 11:26
   */

  public static File excelBytes2PdfFile(byte[] excelBytes, String pdfFilePath) {
    MatchCellLicense.init();
    return Excel2PdfUtil.excelBytes2PdfFile(excelBytes, pdfFilePath);
  }

  /**
   * @description: ppt->pdf
   * @author:
   * @date: 2023/7/18 17:55
   * @param: [pptFilePath, pdfFilePath]
   * @return: void
   **/
  public static void convertPPTtoPDF(String pptxFilePath, String pdfFilePath) {
    try (FileInputStream inputStream = new FileInputStream(pptxFilePath);
         FileOutputStream outputStream = new FileOutputStream(pdfFilePath)) {

      XMLSlideShow pptx = new XMLSlideShow(inputStream);

      PDDocument pdfDoc = new PDDocument();
      File imageTempDir = Files.createTempDirectory("pptx_to_pdf").toFile();

      int pageNum = 0;
      for (XSLFSlide slide : pptx.getSlides()) {

        for (XSLFShape shape : slide.getShapes()) {
          if (shape instanceof XSLFTextShape) {
            XSLFTextShape textShape = (XSLFTextShape) shape;
            for (XSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
              for (XSLFTextRun textRun : textParagraph.getTextRuns()) {
                textRun.setFontFamily("宋体");
              }
            }
          }
        }


        Dimension pageSize = pptx.getPageSize();
        BufferedImage bufferedImage = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setPaint(Color.white);
        graphics.fill(new Rectangle(pageSize));

        slide.draw(graphics);

        File imageFile = new File(imageTempDir, "slide_" + pageNum + ".png");
        ImageIO.write(bufferedImage, "png", imageFile);

        PDPage pdfPage = new PDPage(new org.apache.pdfbox.pdmodel.common.PDRectangle(pageSize.width, pageSize.height));
        pdfDoc.addPage(pdfPage);

        try (PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, pdfPage)) {
          PDImageXObject pdfImage = PDImageXObject.createFromByteArray(pdfDoc, Files.readAllBytes(imageFile.toPath()), "slide_" + pageNum);
          contentStream.drawImage(pdfImage, 0, 0);
        }

        pageNum++;
      }

      pdfDoc.save(outputStream);
      pdfDoc.close();

      // Delete temporary image files
      for (File imageFile : imageTempDir.listFiles()) {
        imageFile.delete();
      }
      imageTempDir.delete();

      System.out.println("PPTX to PDF conversion completed successfully.");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }



}

