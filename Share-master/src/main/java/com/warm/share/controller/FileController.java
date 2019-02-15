package com.warm.share.controller;

import com.warm.share.model.BaseModel;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileController {


    @RequestMapping(path = "/header", method = RequestMethod.POST)
    public BaseModel handleFileUpLoad(HttpServletRequest request, @RequestParam String id, @RequestParam CommonsMultipartFile file) throws IOException {

        //文件上传路径
        String path = request.getServletContext().getRealPath("/images/");
        //上传文件名
        String filename =file.getOriginalFilename();
        File filepath = new File(path, filename);
//判断路径是否存在
        if(!filepath.getParentFile().exists()){
            filepath.getParentFile().mkdirs();
        }

        file.transferTo(filepath);

        return BaseModel.success();
    }


}
