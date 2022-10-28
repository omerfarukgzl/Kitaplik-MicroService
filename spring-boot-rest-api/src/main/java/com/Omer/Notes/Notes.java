package com.Omer.Notes;

public class Notes {
    /*

***************** Entity Oluştruma **********************


entity package oluşturduk ve User Classı oluşturduk(User Table)
Enitiy Classı için @Entity ve @Id anottaion a ihtiyacımız var.
Table Özelliği ile tablo adını girdik
GeneratedValue İle id nin artım ve başlangıç değerinini belirttik
SequenceGenerator
Coulmn özelliği ile sutun başlığı güncellendi
BU entity Classına lombokdan gelen Data annotation eklenince geter seter to string metodları oluşturuldu
Bunun yerine el ile @ geter @ seter eklenebilir

Ortak Tablo olarak baseclass oluiturulur kim güncelledi ne zaman güncelledi vb;
Base Class ı oluşturan Annotttaion @MappedSupperClass dır
ve Serialzable implement ediyoruz : Bu özellik nesnemiiz networken taşıma veyha diske yazıp okuma özelliği kaznaıdıruoruz;




************ Katmanlar Arası Mimari *******************

                   Client
                     ||
                     || İstek
                     \/
                  Controller
                     ||
                     ||    Dto ile veri haberleşme
                     \/
                  Services
                     ||
                     ||   Entity ile haberleşme
                     ||
                     \/
                  Repository ============ > Database
                              interface yardımı






Katmanlı mimaride 3 adet katmanımız var :
*Controller
*Services
*Repository

Controller katmanı : İnternetten gelen sunucuya gelen isteklerin ilk karşılandığı katmandır
Yani dışarıya açtığımız web api katmanıda denebilir

Services Katmanı  : İş kodlarını yazdığımız aldığımız veri kontrolleri validation kontrolleri katmanıdır

Repository Katmanı: Interface yardımıyla database işlşemlerinin yapıldığı katmnadır

Bu Katmanlar arasındaki iletişimi DTO ve entity nesneleri ile gerçekleşir

Controler- Services Dto
Services - Repository Entity


Önce Controller Paketini oluşturduk (api package) controller package de olabilirdi farketmez
Daha sonra controller classı oluşturduk ve
bu classın api olatrak dışarıya açılabilmesi için @Restcontroller annottatiın ekledik
İkinci olarak bu api nin hangi adreste yayınlanacağını belirtmek için @RequestMapping annotation ekledik


Repositoy Katmanını oluşturduk. package name repository
UserRepository Classı oluşturduk. JPA Reposiry extend ettik ve ilk parametre olarak hangi classa hizmet edicekse onu ikinci olarak primary key değeri tipini belirttik (<User,Long>)
Repository için @Repository Annotationunu eklememmize gerek yok çünkü jpa extend edildiği için bunun bir repository katmanı olduğunu anlar
Böylelikle repository interface imize hazır fonksiyonları kullanabilme özelliği kazandırdık db methodları.
Eğer hazır gelen methodlar bu jpa da yoksa örneğin ada göre arama yapılacak method yazacağız o zaman kullanılafak table adını (class adını) daha sonra FindBy edikten sonra firstname alanının seçip method yazabilirz
User FindByFirstname(String firstname){}
eğer burda da bulamazsak @Query  Sorgusuna yazabiliriz.


services katmanını oluşturduk.iş kodları burda oluşturulur.
Katmanlar arası bağlantılar interface üzerinden olduğu için interface oluşturduk.UserServices
Daha Sonra interface leri implemente edicek classlar için servicesImpl paketi açtık. Daha sonra Class oluşturduk.UserServicesImpl
Classı ımızın services olduğunu belirtmek için @Services Annotation unu ekledik.Böylece Proje ayağu kalkerken bu classın instance ı oluşturulacak ve IoC konteynırına eklenecek.




***************** Katmanlar Arası Bağlantı ***********************

***Controller----Services**

Controller Katmanı Services katmanı ile konuşacağı için Controller sınıfına property ekledik.IoC içinde tutulan property ye Inject ettik.


 @Autowired
 private UserServices userServices

  veya constructor ile yapabiliriz. Bu önerilir Çünkü Zorunluluk ortaya koyar oarametre girilmesi gerek constructora ve nullpointer errorundan kurtulururuz.

    private final UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }
    veya   private final UserService userService; yanlızca bunu yazarız ve @RequiredArgsContructor Annotationu ile Constructor u otomatik oluşturur.



******************Not*****************


controllerın ustundeki
@RequestMapping(value = "/user")
ve controller içindeki

    @PostMapping(value = "/create")
    public ResponseEntity<User> createUser(@RequestBody User user)
    {
        User resultUser=userService.createUser(user);
        return ResponseEntity.ok(resultUser);
    }

aslında .net core daki controller url si ve action url sdir  @RequestMapping(value = "/user")   ,  @PostMapping(value = "/create")


******************Not*****************


***************** PostMapping Metodu***********************

Metodumuzu public olarak tanımlıyoruz çünkü dışarıya açaçcağız.
Dönüş Tipini SpringFrameworkden gelen ResponseEntity den dönüyoruz Böylelikle metdolarımıza ortak imza oluşturuyoryuz.
@PostMapping Annotation u ekledik.ve bir path verdik.
İkinci olarak RequestBody Annotation eklendi. Böylece Json nesnemeizi user classımızla eşeltirmeyi sağlıyoruz.
    @PostMapping(value = "/create")
    public ResponseEntity<User> createUser(ResponseBody User user)
    {
         User resultUser=userService.createUser(user);
        return ResponseEntity.ok(resultUser);
    }

    **** İstek Clien tdan Controller tarfından karşılandı. Bu istek create isteğiydi
    daha sonra Controller ile services haberleşti ve services deki createUser Fonksiyonuna gitti burtada işlemler yapıldı(kim tarafından oluşturuldu ne zaman oluşturuldu vs.)
    daha sonra bu fonksiyon işlemler sonucundaki user nesnesini Repository. save e gönderdi
    ve veri tabanı işlemleri repositoryde gerçekleşti (save ile)
    ve daha sonra bu işlem bitiminin sonucu resultUser a kaydedildi
    ve ResponseEntity.ok(resultUser) ile oluşturulan kayıt potmanda görülebidli.




     Daha sonra services katmanınnda da aynı method oluşturlur.Interface Uzerinde
     public abstract olarak tanımlamayız ınterface dekileri çünkü default olarak öyledir zten;
     BU INTERFACE İ İMPLEMENT EDEN UserServicesImpl classında override edelim ve içeride kim tarafından tarihi oluşturalım
     ve daha sonra repository.save fonksiyonuna gönderdik.














******************Not*****************

services katmanlarında interface olmasının sebebi Controller sınıfınfa services Impl sınıfının Kullanılmasından solayı Dependency Injection uygulanmıştır.





******************Not*****************








  */

}
