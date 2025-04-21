package org.mmc.util;//package org.mmc.util;
//
//import org.apache.commons.lang3.StringUtils;
//import org.jaudiotagger.audio.mp3.MP3AudioHeader;
//import org.jaudiotagger.audio.mp3.MP3File;
//import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
//import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.test.video.VideoToImageUtil.log;
//
///**
// * mp3工具类
// *
// <!--获取音频文件标题-->
// <dependency>
// <groupId>org</groupId>
// <artifactId>jaudiotagger</artifactId>
// <version>2.0.3</version>
// </dependency>
// * @author kangyonggan
// * @since 9/4/17
// */
//
//public class Mp3Util {
//
//    public static void main(String[] args) throws Exception {
//        Map<String, Object> resultMap = parse("/Users/kyg/Music/网易云音乐/demo.mp3", "/Users/kyg/Desktop");
////        log.info(resultMap);
//    }
//
//    /**
//     * 解析mp3
//     *
//     * @param mp3Path  mp3路径
//     * @param albumDir 解析出的专辑图片存放目录
//     * @return
//     */
//    public static Map<String, Object> parse(String mp3Path, String albumDir) {
//        Map<String, Object> resultMap = new HashMap();
//
//        FileOutputStream out = null;
//        try {
//            MP3File mp3File = new MP3File(mp3Path);
//            MP3AudioHeader header = mp3File.getMP3AudioHeader();
//            AbstractID3v2Tag id3v2Tag = mp3File.getID3v2Tag();
//
//            // 时长(秒)
//            int duration = header.getTrackLength();
//            // 文件大小
//            long size = mp3File.getFile().length();
//            // 歌曲名
//            String name = getInfo(id3v2Tag, "TIT2");
//            // 歌手
//            String singer = getInfo(id3v2Tag, "TPE1");
//            // 专辑
//            String album = getInfo(id3v2Tag, "TALB");
//            // 专辑图片
//            AbstractID3v2Frame frame = (AbstractID3v2Frame) id3v2Tag.frameMap.get("APIC");
//            String mimeTpe = (String) frame.getBody().getObjectValue("MIMEType");
//            byte data[] = (byte[]) frame.getBody().getObjectValue("PictureData");
//
//            // 根据MIMEType获取对应的后缀，如：image/jpeg -> .jpg
//            String extension = FileUtils.getFileSuffix(mimeTpe);
//            if (!extension.contains(".")) {
//                extension = "." + extension;
//            }
////            String extension = FileUtil.getExtension(mimeTpe);
//            if (StringUtils.isEmpty(extension)) {
//                extension = ".jpg";
//            }
//            // 把专辑图片写入指定文件夹
//            out = new FileOutputStream(albumDir + File.separator + album + extension);
//            out.write(data);
//            out.flush();
//
//            // 写响应
//            resultMap.put("duration", duration);
//            resultMap.put("size", size);
//            resultMap.put("name", name);
//            resultMap.put("singer", singer);
//            resultMap.put("album", album);
//            resultMap.put("mimeTpe", mimeTpe);
//            resultMap.put("albumName", album + extension);
//
//            resultMap.put("respCo", 0);
//            resultMap.put("respMsg", "解析mp3成功");
//        } catch (Exception e) {
//            log.warn("解析mp3信息异常", e);
//            resultMap.put("respCo", -1);
//            resultMap.put("respMsg", e.getLocalizedMessage());
//        } finally {
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException e) {
//                    log.warn("关闭流异常", e);
//                }
//            }
//        }
//
//        return resultMap;
//    }
//
//    /**
//     * 获取歌曲信息
//     *
//     * @param id3v2Tag
//     * @param identifier
//     * @return
//     */
//    private static String getInfo(AbstractID3v2Tag id3v2Tag, String identifier) {
//        AbstractID3v2Frame frame = (AbstractID3v2Frame) id3v2Tag.frameMap.get(identifier);
//        return (String) frame.getBody().getObjectValue("Text");
//    }
//
//}
////package com;
////
////import org.jaudiotagger.audio.AudioFile;
////import org.jaudiotagger.audio.AudioFileIO;
////import org.jaudiotagger.tag.FieldKey;
////import org.mmc.util.FileUtils;
////
////import java.nio.file.Files;
////import java.nio.file.Path;
////import java.nio.file.Paths;
////
////public class Test {
////
////    public static void main(String[] args) {
////        FileUtils.readDirFiles("D:/Backup/Downloads/Desktop/精", (filePath, file) -> {
////
////            try {
////                // 加载音频文件
////                AudioFile audioFile = AudioFileIO.read(file);
////                // 获取音频文件的标签信息
////                String title = audioFile.getTag().getFirst(FieldKey.TITLE);
////
////                if (title != null && !title.trim().isEmpty()) {
////                    // 构造新的文件名，这里简单地使用了标题作为文件名，并保留原始扩展名
////                    Path path = Paths.get(filePath);
////                    String extension = Files.probeContentType(path);
////                    String newFileName = title + "." + getExtensionFromPath(path);
////                    System.out.println("newFileName = " + newFileName + " extension = " + extension);
////                    // 构建新的文件路径
////                    Path newPath = path.resolveSibling(newFileName);
////
////                    // 如果新旧文件名不同，则进行重命名操作
////                    if (!path.equals(newPath)) {
////                        Files.move(path, newPath);
////                        System.out.println("Renamed to: " + newPath.toString());
////                    } else {
////                        System.out.println("No need to rename.");
////                    }
////                } else {
////                    System.out.println("Title not found in metadata.");
////                }
////
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
////    }
////
////    private static String getExtensionFromPath(Path path) {
////        String name = path.getFileName().toString();
////        int lastIndexOfDot = name.lastIndexOf('.');
////        return lastIndexOfDot == -1 ? "" : name.substring(lastIndexOfDot + 1);
////    }
////
////}