package com.earlyspring.example.entity.animal;

import lombok.Data;
import com.earlyspring.ioc.bean.annotation.AutoWired;
import com.earlyspring.ioc.bean.annotation.Component;

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
