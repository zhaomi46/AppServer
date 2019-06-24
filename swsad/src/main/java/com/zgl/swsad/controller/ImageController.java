package com.zgl.swsad.controller;

import com.zgl.swsad.authorization.annotation.Authorization;
import com.zgl.swsad.config.Constants;
import com.zgl.swsad.model.Image;
import com.zgl.swsad.util.FileUtil;
import com.zgl.swsad.util.ReturnMsg;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
public class ImageController {
    @RequestMapping(value="/images", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseEntity<Object> uploadImg(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            new ResponseEntity(new ReturnMsg("uploadimg fail"), HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        String old_fileName = file.getOriginalFilename();
        String type = old_fileName.substring(old_fileName.indexOf('.'));
        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + type;
        try {
            FileUtil.uploadFile(file.getBytes(), Constants.IMAGEPATH, fileName);
            System.out.println(contentType + fileName);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return new ResponseEntity(new Image(fileName), HttpStatus.CREATED);
    }
}
