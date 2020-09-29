package com.web.entity.animal;

import lombok.Data;
import org.earlyspring.bean.annotation.AutoWired;
import org.earlyspring.bean.annotation.Component;
import org.earlyspring.bean.annotation.Qualifier;

/**
 * @Author: czf
 * @Date: 2020/5/29 14:40
 */
@Component("lion")
@Data
public class Lion {
    @AutoWired
    private Tiger tiger;
}
