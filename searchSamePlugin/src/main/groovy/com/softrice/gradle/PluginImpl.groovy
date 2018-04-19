package com.softrice.gradle

import com.softrice.gradle.logcat.PrintUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.security.MessageDigest

public class PluginImpl implements Plugin<Project> {

    List<FileContent> fileMD5List
    List<File> fileList
    SearchSameExt searchSameExt

    void apply(Project project) {

        project.extensions.create('searchSameExt', SearchSameExt)


        project.afterEvaluate {
            searchSameExt = project.extensions.findByName("searchSameExt") as SearchSameExt
            
            if(searchSameExt.enable) {
                fileList = new ArrayList<>()
                fileMD5List = new ArrayList<>()
                PrintUtil.info("start finding the same files" )
                dfsFiles(project.getProjectDir().absolutePath + "/src")
                judgeSame()
                PrintUtil.info("end finding the same files")
            }
        }
    }

    void dfsFiles(String dir) {
        File[] files = new File(dir).listFiles()
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file)
                } else if (file.isDirectory()) {
                    dfsFiles(file.absolutePath)
                }
            }
        }
    }

    void judgeSame() {
        if(fileList.isEmpty()) {
            PrintUtil.info("fileList is empty")
            return
        }
        Map<String,List<FileContent>> sameFileList = new HashMap<>();
        for(File file : fileList) {
            String md5 = fileToMD5(file.absolutePath)
            FileContent fileContent = new FileContent(md5,file)

            if(fileMD5List.contains(fileContent)) {
                if(sameFileList.get(md5) == null) {
                    sameFileList.put(md5,new ArrayList<FileContent>())
                    int index = fileMD5List.indexOf(fileContent)
                    if(index >= 0)
                        sameFileList.get(md5).add(fileMD5List.get(index))
                }
                sameFileList.get(md5).add(fileContent)
            }
            fileMD5List.add(fileContent)
        }

        for(String md5 : sameFileList.keySet()) {
            List<FileContent> fileContentList = sameFileList.get(md5)
            PrintUtil.warn("finds " + fileContentList.size() + " files are same")
            for(FileContent fileContent : fileContentList) {
                PrintUtil.warn(fileContent.file)
            }
        }

    }

    String fileToMD5(String path){
        try {
            FileInputStream fis = new FileInputStream(path)
            MessageDigest digest = MessageDigest.getInstance("MD5")
            byte[] buffer = new byte[1024];
            int len
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            fis.close();
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return  bigInt.toString(16);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
