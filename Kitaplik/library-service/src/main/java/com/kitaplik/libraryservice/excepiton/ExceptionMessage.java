package com.kitaplik.libraryservice.excepiton;

public record ExceptionMessage (String timestamp,
                                int status,
                                String error,
                                String message,
                                String path){}
// exception message fırlatacağım error da ki hanggi fieldların olması gerektiğini belitiryorum
//record ==> jvm de final class oluşturuyor. bu final classın içerisinde constructor lar bulunuyor ve getter setter methodları ouşturuyor
// neden o zaman tüm classları record yapmıyoruz getter setter allconstructure vs ile uğraşıyoruz?
// çünkü final class jpa da kullanılamıyor. extend edilemiyor
// fakat dto lar kullanılablir!!