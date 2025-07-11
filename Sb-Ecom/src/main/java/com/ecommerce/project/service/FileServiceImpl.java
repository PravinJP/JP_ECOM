package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //File name of the current file
        String OriginalFileName=file.getOriginalFilename();
        //Generate the unique file name
        String randomId= UUID.randomUUID().toString();
        String fileName=randomId.concat(OriginalFileName.substring(OriginalFileName.lastIndexOf('.')));

        String filePath=path + File.separator+fileName;

        //Check if the path exists and create
        File folder=new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }
        //upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //return the file name
        return fileName;
    }
}
