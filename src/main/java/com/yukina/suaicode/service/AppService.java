package com.yukina.suaicode.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yukina.suaicode.model.dto.app.AppQueryRequest;
import com.yukina.suaicode.model.entity.App;
import com.yukina.suaicode.model.entity.User;
import com.yukina.suaicode.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author <a href="https://github.com/Yukimiyi">yukina</a>
 */
public interface AppService extends IService<App> {

    AppVO getAppVO(App app);

    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    List<AppVO> getAppVOList(List<App> appList);

    Flux<String> chatToGenCode(Long appid, String message, User loginUser);

    String deployApp(Long appid, User loginUser);
}
