package com.yukina.suaicode.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.yukina.suaicode.model.entity.App;
import com.yukina.suaicode.mapper.AppMapper;
import com.yukina.suaicode.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author <a href="https://github.com/Yukimiyi">yukina</a>
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
