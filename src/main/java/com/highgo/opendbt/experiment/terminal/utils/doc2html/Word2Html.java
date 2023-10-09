package com.highgo.opendbt.experiment.terminal.utils.doc2html;

import com.highgo.opendbt.experiment.terminal.utils.doc2html.bean.dto.DocHtmlDto;
import com.highgo.opendbt.experiment.terminal.utils.doc2html.bean.dto.WordHtmlDto;
import com.highgo.opendbt.experiment.terminal.utils.doc2html.config.Config;

import fr.opensagres.poi.xwpf.converter.core.BasicURIResolver;
import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.core.ImageManager;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

public class Word2Html implements Doc2Html {

  @Override
  public DocHtmlDto doc2Html(String filePath) throws IOException {
    if (filePath.indexOf(".") >= 0) {
      WordHtmlDto resultDto = null;
      String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
      if (fileType.equalsIgnoreCase("doc")) {
        try {
          resultDto = docToHtml(filePath);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (fileType.equalsIgnoreCase("docx")) {
        try {
          resultDto = docxToHtml(filePath);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return resultDto;
    }
    return null;
  }

  // docת��Ϊhtml
  private WordHtmlDto docToHtml(String filePath) throws Exception {
    HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(filePath));
    WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
    // ����ͼƬ�����·��
    wordToHtmlConverter.setPicturesManager((a, b, suggestedName, d, e) -> "image" + File.separator + suggestedName);
    wordToHtmlConverter.processDocument(wordDocument);
    List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();

    String outDir = Config.getDir(System.currentTimeMillis() + "");
    File dirFile = new File(outDir);
    if (!dirFile.exists()) {
      dirFile.mkdirs();
    }
    StringBuilder imageDir = new StringBuilder();
    imageDir.append(outDir).append("/image");
    File imagrDirFile = new File(imageDir.toString());
    if (!imagrDirFile.exists()) {
      imagrDirFile.mkdirs();
    }
    for (Picture pic : pics) {
      // ����ͼƬ
      StringBuilder imageFilePath = new StringBuilder();
      imageFilePath.append(imageDir).append("/").append(pic.suggestFullFileName());
      pic.writeImageContent(new FileOutputStream(imageFilePath.toString()));
    }
    Document htmlDocument = wordToHtmlConverter.getDocument();
    DOMSource domSource = new DOMSource(htmlDocument);
    StringBuilder htmlPath = new StringBuilder();
    htmlPath.append(outDir).append("/content.html");
    StreamResult streamResult = new StreamResult(new File(htmlPath.toString()));

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer serializer = tf.newTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.setOutputProperty(OutputKeys.METHOD, "html");
    serializer.transform(domSource, streamResult);

    WordHtmlDto resultDto = new WordHtmlDto();
    resultDto.setHtml(htmlPath.toString());
    return resultDto;
  }

  // docxת��Ϊhtml
  private WordHtmlDto docxToHtml(String filePath) throws Exception {
    FileOutputStream fileOutputStream = null;
    OutputStreamWriter outputStreamWriter = null;
    WordHtmlDto resultDto = null;

    try {
      XWPFDocument document = new XWPFDocument(new FileInputStream(filePath));
      XHTMLOptions options = XHTMLOptions.create();

      String outDir = Config.getDir(System.currentTimeMillis() + "");
      File dirFile = new File(outDir);
      if (!dirFile.exists()) {
        dirFile.mkdirs();
      }
      StringBuilder imageDir = new StringBuilder();
      imageDir.append(outDir).append("/image");
      File imagrDirFile = new File(imageDir.toString());
      if (!imagrDirFile.exists()) {
        imagrDirFile.mkdirs();
      }
      options.setExtractor(new FileImageExtractor(new File(imageDir.toString())));
      // html��ͼƬ��·��
      options.URIResolver(new BasicURIResolver("image"));
      StringBuilder htmlPath = new StringBuilder();
      htmlPath.append(outDir).append("/content.html");
      fileOutputStream = new FileOutputStream(htmlPath.toString());
      outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
      XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
      xhtmlConverter.convert(document, outputStreamWriter, options);
      resultDto = new WordHtmlDto();
      resultDto.setHtml(htmlPath.toString());

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (outputStreamWriter != null) {
        outputStreamWriter.close();
      }
      if (fileOutputStream != null) {
        fileOutputStream.close();
      }
    }
    return resultDto;
  }

}
