package com.splittree.penum;

/**
 * Created by yangmingquan on 2018/9/30.
 */
public enum  TccPatternEnum {

    /**
     * Tcc tcc pattern enum.
     */
    TCC(1, "try,confirm,cancel模式"),

    /**
     * Cc tcc pattern enum.
     */
    CC(2, "confirm,cancel模式");

    private Integer code;

    private String desc;

    TccPatternEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(final Integer code) {
        this.code = code;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(final String desc) {
        this.desc = desc;
    }
}
