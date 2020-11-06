package com.wudi.socket.compare;

import java.sql.SQLOutput;

/**
 * @author Dillon Wu
 * @Description:==比较的是地址，用equals比较八大类时比较的值，但是
 * 在equals()比较自定义的对象时比较的依然是引用地址，若要比较值，需要重写equals()
 * @date 2020/10/28 9:46
 */
public class Demo1 {
    private int c=128;
    public static void main(String[] args) {
      originalString();
      string();
    }

    public static void originalString(){
        String str1 = new String("abc");
        String str2 =new String("abc");
        System.out.println("new String ==:"+(str1 == str2));
        System.out.println("new String equals:"+(str1.equals(str2)));
        System.out.println("new String compareTO:"+(str1.compareTo(str2))+"\t");
    }

    public static void string(){
        String str1 = "abc";
        String str2 ="abc";
        System.out.println("string ==:"+(str1 == str2));
        System.out.println("string equals:"+(str1.equals(str2)));
        System.out.println("string compareTO:"+(str1.compareTo(str2)));
        Integer a=128;
        Integer b=128;
        System.out.println("string == a到b:"+(a == b));
        System.out.println("string equals a到c:"+(a.equals(b)));



    }
}
