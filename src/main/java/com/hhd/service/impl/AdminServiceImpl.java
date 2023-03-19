package com.hhd.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhd.exceptionhandler.CloudException;
import com.hhd.mapper.AdminMapper;
import com.hhd.mapper.FileMapper;
import com.hhd.mapper.UcenterMapper;
import com.hhd.pojo.domain.Admin;
import com.hhd.pojo.domain.UCenter;
import com.hhd.pojo.entity.Files;
import com.hhd.service.IAdminService;
import com.hhd.utils.InitOssClient;
import com.hhd.utils.JwtUtils;
import com.hhd.utils.MD5;
import com.hhd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import wiremock.org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hhd.utils.InitVodClient.initVodClient;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author -无心
 * @since 2023-02-16
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Autowired
    private AdminMapper aMapper;
    @Autowired
    private UcenterMapper uMapper;
    @Autowired
    private FileMapper fMapper;

    private static final String VIDEO = "video";
    private static final String AUDIO = "audio";
    @Autowired
    private RedisTemplate<String, String> template;
    private static final ThreadLocal<DateFormat> THREAD_LOCAL = ThreadLocal.withInitial(() ->
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    @Override
    public Map<String, Admin> login(Admin admin) {
        String loginName = admin.getLoginName();
        String password = admin.getPassword();
        String code = admin.getCode();
        if (loginName.isEmpty() || password.isEmpty()) {
            throw new CloudException(R.ERROR, R.EMPTY_ERROR);
        }
        LambdaUpdateWrapper<Admin> lqw = new LambdaUpdateWrapper<>();
        Admin exist = aMapper.selectOne(lqw.eq(Admin::getLoginName, loginName));
        if (exist == null) {
            throw new CloudException(R.ERROR, R.NON_REGISTER);
        }
        if (!MD5.encrypt(password).equals(exist.getPassword())) {
            throw new CloudException(R.ERROR, R.PASSWORD_ERR);
        }
        String code1 = template.opsForValue().get("checkCode");
        if (!code.equalsIgnoreCase(code1)) {
            throw new CloudException(R.ERROR, R.CHECK_ERROR);
        }
        String token = JwtUtils.getJwtToken(exist);
        Map<String, Admin> map = new HashMap<>(1);
        map.put(token, exist);
        return map;
    }

    @Override
    public int insert(Admin admin) {
        return aMapper.insert(admin);
    }

    @Override
    public Admin selectOne(String id) {
        LambdaUpdateWrapper<Admin> lqw = new LambdaUpdateWrapper<>();
        return aMapper.selectOne(lqw.eq(Admin::getAid, id));
    }

    @Override
    public List<UCenter> showNormalAll() {
        return uMapper.selectList(null);
    }

    @Override
    public List<UCenter> showRecoveryAll() {
        DateTime nowTime = new DateTime();
        return uMapper.showRecoveryAll(THREAD_LOCAL.get().format(nowTime));
    }

    @Override
    public List<Files> showAllFiles() {
        return fMapper.showRecoveryAllFiles();
    }

    @Override
    public boolean changeStatus(UCenter uCenter) {
        return uMapper.changeStatus(uCenter.getId(), uCenter.getStatus());
    }

    @Override
    public int logicDelUser(String id) {
        LambdaUpdateWrapper<UCenter> lqw = new LambdaUpdateWrapper<>();
        return uMapper.update(new UCenter(),
                lqw.set(UCenter::getLogicDelTime, THREAD_LOCAL.get().format(DateUtils.addDays(new DateTime(), 30)))
                        .eq(UCenter::getId, id));
    }

    @Override
    public int logicNormalUser(String id) {
        return uMapper.logicNormalUser(id);
    }

    @Override
    public int delUserById(String id) {
        return aMapper.delById(id);
    }

    @Override
    public R delFileByMd5(Files files) {
        R r = R.ok();
        aMapper.delByMd5(files.getMd5());
        String videoId = files.getVideoId();
        String createTime = new DateTime(files.getCreateTime()).toDateStr();
        String fileName = files.getFileName();
        String type = files.getType();
        if (VIDEO.equals(files.getFileType()) || AUDIO.equals(files.getFileType())) {
            try {
                //初始化对象
                DefaultAcsClient client = initVodClient();
                //创建删除视频request对象
                DeleteVideoRequest request = new DeleteVideoRequest();
                //向request设置视频id
                request.setVideoIds(videoId);
                //调用初始化对象的方法实现删除
                client.getAcsResponse(request);
            } catch (Exception e) {
                throw new CloudException(R.ERROR, R.DELETE_VA_ERR);
            }
        } else {
            OSS ossClient = new OSSClientBuilder().build(InitOssClient.END_POINT,
                    InitOssClient.ACCESS_KEY_ID, InitOssClient.ACCESS_KEY_SECRET);
            try {
                String fileKey = createTime.substring(0, 10) + "/" + fileName + "." + type;
                ossClient.deleteObject(InitOssClient.BUCKET_NAME, fileKey);
            } catch (Exception e) {
                return R.error();
            } finally {
                ossClient.shutdown();
            }
        }
        return r;
    }

    @Override
    public List<UCenter> searchUsers(UCenter uCenter) {
        return uMapper.searchByFuzzyUsers(uCenter);
    }

    @Override
    public List<Files> searchFiles(Files files) {
        return fMapper.searchFuzzyFiles(files);
    }


}
