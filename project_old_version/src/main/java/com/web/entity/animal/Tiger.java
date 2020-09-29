package com.web.entity.animal;

import lombok.Data;
import org.earlyspring.bean.annotation.AutoWired;
import org.earlyspring.bean.annotation.Component;

/**
 * @Author: czf
 * @Date: 2020/5/29 14:40
 */
@Component("tiger")
@Data
public class Tiger {
    @AutoWired
    private Lion lion;
}
