package org.freeone.util;

import japa.parser.ast.PackageDeclaration;

/**
 * 平台工具，用于识别当前类是哪个平台的
 */
public class PlatformUtil {
    /**
     * 通过PackageDeclaration中转成字符串之后判断平台
     * @param packageDeclaration package生命
     * @return 平台字符串
     */
    public static String getPlatform(PackageDeclaration packageDeclaration){
        if (packageDeclaration != null){
            String packageString = packageDeclaration.getName().toString().toLowerCase();
            if (packageString.contains(".taobao")){
                return "taobao";
            }else if (packageString.contains(".jingdong")){
                return "jingdong";
            }else if (packageString.contains(".pinduoduo")){
                return "pinduoduo";
            }else if (packageString.contains(".douyin")){
                return "douyin";
            }else if (packageString.contains(".kuaishou")){
                return "kuaishou";
            }else if (packageString.contains(".ali1688")){
                return "ali1688";
            }else if (packageString.contains(".suning")){
                return "suning";
            }
        }
        return null;
    }

    public static void main(String[] args) {

    }
}
