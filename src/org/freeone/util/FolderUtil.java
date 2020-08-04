package org.freeone.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹相关
 * @author Lq<sup>2<sup/>
 */
public class FolderUtil {
    /**
     * 清空文件夹，如果文件不存在,则生成文件，如果文件存在则删除文件夹下的所有内容
     * @param folderPath
     *
     */
    public static void clearFolderContent(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }else {
            File[] files = folder.listFiles();
            for (File file : files) {
                deleteFolderAndFile(file.getAbsolutePath());
            }

        }
    }
    /**
     * 删除文件和文件夹
     * @param path
     */
    public static void deleteFolderAndFile(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File childFile : files) {
                if (childFile.isDirectory()) {
                    deleteFolderAndFile(childFile.getAbsolutePath());
                }else{
                    childFile.delete();
                }
            }
            file.delete();
        }else {
            file.delete();
        }
    }

    /**
     * 获取目录下的所有文件夹
     * @param path
     * @return
     */
    public static List<String> getFolderListInPath(String path){
        List<String> folderList = new ArrayList<>();
        File baseFile = new File(path);
        if (baseFile.isFile() || !baseFile.exists()) {
            return folderList;
        }
        File[] listFiles = baseFile.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()){
                folderList.add(file.getAbsolutePath().replace("\\", "/"));
                folderList.addAll(getFolderListInPath(file.getAbsolutePath().replace("\\", "/")));
            }
        }
        return folderList;
    }

    /**
     * 批量创建文件夹
     * @param folderList
     */
    public static void createFolders(List<String> folderList){
        if (folderList == null || folderList.isEmpty()){
            return;
        }
        for (String folderPath : folderList) {
            File file = new File(folderPath);
            if (!file.exists()){
                file.mkdirs();
            }
        }
    }

    /**
     * 将源文件夹下的文件夹集合转换成目标文件夹下的路径
     * @param originFolderList
     * @param originPath
     * @param targetPath
     * @return
     */
    public static List<String> convertToPathInTargetFolder(List<String> originFolderList,String originPath,String targetPath){
        List<String> folderListInTargetPath = new ArrayList<>();
        if (originFolderList == null || originFolderList.isEmpty() || StringUtils.isEmpty(targetPath)){
            return folderListInTargetPath;
        }
        for (String originFolder : originFolderList) {
            folderListInTargetPath.add(originFolder.replace(originPath, targetPath));
        }
        return folderListInTargetPath;
    }
}
