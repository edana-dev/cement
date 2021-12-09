package com.maneki.cement.dbconfig.core;

import lombok.Data;

@Data
public class PropertyItem {
    /**
     * 配置属性名称
     */
    private String name;
    /**
     * 属性类型, 包括：json, string, boolean, int
     */
    private String type;
    /**
     * 属性值
     */
    private String value;
}
