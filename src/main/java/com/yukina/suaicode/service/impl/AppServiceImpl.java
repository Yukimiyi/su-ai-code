package com.yukina.suaicode.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yukina.suaicode.core.AiCodeGeneratorFacade;
import com.yukina.suaicode.exception.BusinessException;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.exception.ThrowUtils;
import com.yukina.suaicode.mapper.AppMapper;
import com.yukina.suaicode.model.dto.app.AppQueryRequest;
import com.yukina.suaicode.model.entity.App;
import com.yukina.suaicode.model.entity.User;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;
import com.yukina.suaicode.model.vo.AppVO;
import com.yukina.suaicode.model.vo.UserVO;
import com.yukina.suaicode.service.AppService;
import com.yukina.suaicode.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yukina.suaicode.constant.AppConstant.*;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/Yukimiyi">yukina</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }


    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        Set<Long> userIds = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            appVO.setUser(userVOMap.get(app.getUserId()));
            return appVO;
        }).toList();
    }

    @Override
    public Flux<String> chatToGenCode(Long appid, String message, User loginUser) {
        // 1. 参数验证
        ThrowUtils.throwIf(appid == null || appid <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(message == null || message.length() == 0, ErrorCode.PARAMS_ERROR);
        // 2. 查询应用
        App app = this.getById(appid);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 权限验证
        if (!app.getUserId().equals(loginUser.getId())) {
            ThrowUtils.throwIf(true, ErrorCode.NO_AUTH_ERROR, "没有权限操作该应用");
        }
        // 4. 获取代码生成类型
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        // 5. 调用代码生成器
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(codeGenTypeEnum, message, appid);
    }

    @Override
    public String deployApp(Long appid, User loginUser) {
        // 1.参数校验
        ThrowUtils.throwIf(appid == null || appid <= 0, ErrorCode.PARAMS_ERROR, "应用ID为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2.检验应用信息
        App app = this.getById(appid);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 3.检验是否有权限，仅本人可部署
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "没有权限操作该应用");
        // 4.检查是否有deployKey
        String deployKey = app.getDeployKey();
        // 不存在则生成
        if (StrUtil.isBlank(deployKey)) {
            deployKey = "deployKey_" + RandomUtil.randomNumbers(6);
        }
        // 5.获取代码生成类型，构造源目录
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appid;
        String sourceDirPath =  CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        File sourceDir = new File(sourceDirPath);
        // 6.检查源目录是否存在
        ThrowUtils.throwIf(!sourceDir.exists() || !sourceDir.isDirectory(), ErrorCode.SYSTEM_ERROR, "源目录不存在");
        String deployDirPath =  CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        // 7.复制文件到部署目录
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败" + e.getMessage());
        }
        // 8. 更新数据库, deployKey和deployTime
        App updateApp = new App();
        updateApp.setId(appid);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean update = this.updateById(updateApp);
        ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "更新部署时间失败");
        // 9. 返回可访问的 URL
        return String.format("%s/%s/", CODE_DEPLOY_HOST, deployKey);
    }
}
