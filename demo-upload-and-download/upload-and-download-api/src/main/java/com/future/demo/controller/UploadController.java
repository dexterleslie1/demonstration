package com.future.demo.controller;

import com.future.demo.exception.BusinessException;
import com.future.demo.http.ObjectResponse;
import com.future.demo.util.Const;
import com.future.demo.util.Util;
import com.future.demo.vo.SliceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/upload")
@Slf4j
public class UploadController {
    /**
     * 流式上传
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping(value = "stream")
    public ObjectResponse<String> stream(@RequestParam(value = "file", required = false) MultipartFile[] files) throws IOException {
        if(files==null || files.length<=0) {
            throw new IllegalArgumentException("没有指定上传文件");
        }

        List<MultipartFile> multipartFiles = new ArrayList<>();
        for(MultipartFile fileTemporary : files) {
            if(!fileTemporary.isEmpty()) {
                multipartFiles.add(fileTemporary);
            }
        }

        log.debug("上传文件总数:{}", multipartFiles.size());

        File temporaryDirectory = Util.getTemporaryDirectory();
        for(MultipartFile fileTemporary : multipartFiles) {
            if(fileTemporary.isEmpty()) {
                continue;
            }
            String originalFilename = fileTemporary.getOriginalFilename();
            File file = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded + File.separator + originalFilename);
            fileTemporary.transferTo(file);

            String name = fileTemporary.getName();
            String contentType = fileTemporary.getContentType();
            long size = fileTemporary.getSize();

            log.debug("上传文件 提交表单名:{}, 文件名称:{}, 文件类型:{}, 大小:{}字节, 临时保存路径:{}"
                    , name, originalFilename, contentType, size, file.getAbsolutePath());
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData("成功上传" + multipartFiles.size() + "个文件");
        return response;
    }

    // 判断分片是否已上传
    @PostMapping("sliceExists")
    public ObjectResponse<SliceVO> sliceExists(@RequestBody(required = false) SliceVO sliceVO) throws BusinessException {

        if(sliceVO==null) {
            throw new IllegalArgumentException("没有指定sliceVO参数");
        }

        String fileMd5 = sliceVO.getFileMd5();
        if(StringUtils.isEmpty(fileMd5)) {
            throw new IllegalArgumentException("没有指定文件md5");
        }

        List<SliceVO.Slice> slices = sliceVO.getSlices();
        if(slices==null || slices.size()<=0) {
            throw new IllegalArgumentException("没有指定文件分片信息");
        }

        // 判断文件分片是否已经上传
        File temporaryDirectory = Util.getTemporaryDirectory();
        for(SliceVO.Slice slice : slices) {
            String sliceMd5 = slice.getMd5();
            String sliceFileName = fileMd5 + "#" + sliceMd5 + ".slice";
            File temporaryFile = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectorySliceUploaded + File.separator + sliceFileName);
            boolean exists = temporaryFile.exists();
            slice.setExists(exists);
        }

        ObjectResponse<SliceVO> response = new ObjectResponse<>();
        response.setData(sliceVO);
        return response;
    }

    // 上传分片
    @PostMapping(value = "sliceUpload")
    public ObjectResponse<String> sliceUpload(@RequestParam(value = "fileMd5", defaultValue = "") String fileMd5,
                                              @RequestParam(value = "sliceMd5", defaultValue = "") String sliceMd5,
                                              @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        if(StringUtils.isEmpty(fileMd5)) {
            throw new IllegalArgumentException("没有指定文件md5");
        }

        if(StringUtils.isEmpty(sliceMd5)) {
            throw new IllegalArgumentException("没有指定分片md5");
        }

        if(file==null || file.isEmpty()) {
            throw new IllegalArgumentException("没有指定上传文件");
        }

        File temporaryDirectory = Util.getTemporaryDirectory();
        String filename = fileMd5 + "#" + sliceMd5 + ".slice";
        File fileSlice = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectorySliceUploaded + File.separator + filename);
        file.transferTo(fileSlice);

        // 判断sliceMd5是否和实际上传的分片md5符合
        String uploadSliceMd5 = Util.getFileMd5(fileSlice);
        if(!sliceMd5.equals(uploadSliceMd5)) {
            String message = String.format("上传分片md5=%s 和 客户端计算md5=%s不相符", uploadSliceMd5, sliceMd5);
            fileSlice.delete();
            throw new BusinessException(message);
        }

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();

        String message = String.format("成功上传分片，分片名称=%s, 文件类型=%s, 大小=%d字节, 临时保存路径=%s", originalFilename, contentType, size, fileSlice.getAbsolutePath()) ;
        log.debug(message);

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setData(message);
        return response;
    }

    // 合并分片为一个整体文件
    @PutMapping(value = "sliceMerge")
    public ObjectResponse<String> sliceMerge(@RequestBody(required = false) SliceVO sliceVO) throws IOException {
        if(sliceVO==null) {
            throw new IllegalArgumentException("没有指定sliceVO参数");
        }

        String filename = sliceVO.getFilename();
        if(StringUtils.isEmpty(filename)) {
            throw new IllegalArgumentException("没有指定文件名");
        }

        String fileMd5 = sliceVO.getFileMd5();
        if(StringUtils.isEmpty(fileMd5)) {
            throw new IllegalArgumentException("没有指定文件md5");
        }

        List<SliceVO.Slice> slices = sliceVO.getSlices();
        if(slices==null || slices.size()<=0) {
            throw new IllegalArgumentException("没有指定文件分片信息");
        }

        // 判断分片是否已设置order
        for(SliceVO.Slice slice : slices) {
            int order = slice.getOrder();
            if(order<=0) {
                String message = String.format("分片md5=%s,order=%d没有指定order", slice.getMd5(), order);
                throw new IllegalArgumentException(message);
            }
        }

        // 分片排序
        Collections.sort(slices, new Comparator<SliceVO.Slice>() {
            @Override
            public int compare(SliceVO.Slice o1, SliceVO.Slice o2) {
                return o1.getOrder() - o2.getOrder();
            }
        });

        // 判断分片是否顺序递增
        int previousOrder = 0;
        for(SliceVO.Slice slice : slices) {
            int order = slice.getOrder();
            if(order-previousOrder!=1) {
                String message = String.format("分片md5=%s,order=%d order不是严格递增错误", slice.getMd5(), order);
                throw new IllegalArgumentException(message);
            }
            previousOrder = order;
        }

        // 判断所有分片是否存在
        File temporaryDirectory = Util.getTemporaryDirectory();
        for(SliceVO.Slice slice : slices) {
            String sliceMd5 = slice.getMd5();
            String sliceFileName = fileMd5 + "#" + sliceMd5 + ".slice";
            File temporaryFile = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectorySliceUploaded + File.separator + sliceFileName);
            boolean exists = temporaryFile.exists();
            if(!exists) {
                String message = String.format("分片md5=%s,order=%d 不存在", slice.getMd5(), slice.getOrder());
                throw new IllegalArgumentException(message);
            }
        }

        // 合并分片
        File targetFile = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectoryUploaded + File.separator + filename);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(targetFile);

            for (SliceVO.Slice slice : slices) {
                String sliceMd5 = slice.getMd5();
                String sliceFileName = fileMd5 + "#" + sliceMd5 + ".slice";
                File temporaryFile = new File(temporaryDirectory, Const.DemoDirectoryName + File.separator + Const.DirectorySliceUploaded + File.separator + sliceFileName);
                long fileSize = temporaryFile.length();
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(temporaryFile);
                    StreamUtils.copyRange(inputStream, outputStream, 0, fileSize - 1);
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                        inputStream = null;
                    }
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(outputStream!=null) {
                outputStream.close();
                outputStream = null;
            }
        }

        log.debug("合并后文件 {}", targetFile.getAbsolutePath());

        // 合并成功后验证文件md5
        String targetFileMd5 = Util.getFileMd5(targetFile);
        if(!targetFileMd5.equals(fileMd5)) {
            String message = String.format("合并后文件 md5=%s 和 客户端计算md5=%s不相符", targetFileMd5, fileMd5);
            throw new BusinessException(message);
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        String message = String.format("分片合并成功，合并后文件 %s", targetFile.getAbsolutePath());
        response.setData(message);
        return response;
    }
}
