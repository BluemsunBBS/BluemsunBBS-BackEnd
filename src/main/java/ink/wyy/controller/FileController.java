package ink.wyy.controller;

import ink.wyy.auth.LoginAuth;
import ink.wyy.bean.APIResult;
import ink.wyy.util.UUIDUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@RestController
public class FileController {

    @PostMapping("/file/upload")
    @LoginAuth
    public APIResult upload(MultipartFile file, HttpServletRequest request) {
        String fileName = null;
        if (file != null) {
            String originFileName = file.getOriginalFilename();
            if (originFileName != null && !"".contentEquals(originFileName)) {
                // 获取扩展名
                String extName = originFileName.substring(originFileName.lastIndexOf("."));
                // 重新生成一个新的文件名
                fileName = UUIDUtil.get() + extName;
                String rootPath = request.getServletContext().getRealPath("/") + "files/";
                if (".jpg|.jpeg|.bmp|.png|.gif|.mp4|.wmv".contains(extName)) {
                    rootPath += "img/";
                }
                // 指定存储文件的根目录
                File dirFile = new File(rootPath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                // 将上传的文件复制到新的文件（完整路径）中
                try {
                    file.transferTo(new File(rootPath + fileName));
                    return APIResult.createOk(fileName);
                } catch (Exception e) {
                    return APIResult.createNg(e.getMessage());
                }
            }
        }
        return APIResult.createNg("文件不能为空");
    }

    @GetMapping(value = "/file/download")
    @LoginAuth
    public ResponseEntity<byte[]> download(@RequestParam("file_name") String fileName, HttpServletResponse response, HttpServletRequest request) {
        String rootPath = request.getServletContext().getRealPath("/") + "files/";
        File file = new File(rootPath + fileName);
        //判断文件是否为空
        if (file.exists()) {
            //生成文件名
            HttpHeaders headers = null;
            try {
                //请求头
                headers = new HttpHeaders();
                //解决文件名乱码
                String downloadFileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
                //让浏览器知道用attachment（下载方式）打开图片
                headers.setContentDispositionFormData("attachment", downloadFileName);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
