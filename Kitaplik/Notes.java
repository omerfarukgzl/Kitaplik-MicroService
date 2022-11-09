public class Notes {

    /*                      ----------------
                           |               |
                istek      |     Book      |
    Client ----------->api |               |------------Db
                           |    Library    |
                           |               |
                           ----------------

Molitik bir uygulamada client istek attığı zaman book apiye veya library apiye bu apiler aynı jvm ( java virtual machine ) üzerinde çalıştırklaır için
    aynı kaynağı ortak kullanırlar ve bu kullanımla birlikte cevap verirler.
        Fakat uygulamamızın kullanıcısının artması service'lerin çoğalması yazılım geliştirlmesi süresinin uzaması ve kaynak sorunları gibi buna benzer sebepler nedeniyle
            bu apilerin ayrılarak yeni bir proje gibi serviceler halinde oluşturuldu. Bu servicler kendi kaynaklarını kendi db lerini kullanmaya başladılar.
                Ayrı küçük jvm ler üzerinde çalışmaya başladılar.

                        istek
                    -------------->  Book Service ---------------->Db
                                            |
                                            |
          Client                            | Rest Template
                                            |
                       istek                |
                    --------------> Library Service ------------ ->Db


Bu oluşturulan ayrı service'ler Rest Template üzerinden haberleşmeye başladılar. Fakat rest template haberleşme altaypısında erişmek istediğimiz api nin url nin tam halini bilmemiz gerekiyor.
    örneğin Book service'in api url değiştiği zaman library service'de de değişiklik yapmam gerkiyor, yada  portu değiştiği zaman,yine library service de değişklik yapmamız gerkeiyor.
    Bu sorun yine projeler arasında dependency oluştu.


                                                 Book Db

                                                    |----------------------------------------------------- |
                                                   \/                                                      |
                istek-------------------------->  Book Service ---------------->Db                         |
                                          Eureka client                                                   |
                                                    | registry# book-service,localhost//:8080 vs                    |
                                                    |1)                                                   |
                                                   \/                                                     |
          Client                         Eurake Server                                                    |localhost:8080/v1/book
                                           |       /\                                                     | Feign Client
                           localhost//:8080|       |    2)                                                |
                                     3)   \/       | discovery# book-service                             |
                                           Eureka client                                                   |
                istek -------------------------> Library Service ------------ ->Db                          |
                                                    |                                                     |
                                                    |------------------------------------------------------

                                                Library Db


Rest template haberleşmesi bağımlılığından dolayı araya yeni bir teknoloji olan eureka server teknolojisi oluşturuldu.

    Eureka client: Microservice içinde bulunan bir özellik.

Book service library service ile çalışacağı zaman eureka client ile eureka server'a kullandığı özellikler ile ( ben book service, ip adresim localhost//xx, benim portum 8080 vs) kayıt oluyor.

Daha sonra library service book service deki book-service/v1/book apisine ulaşması gerektiğini eureka server'a bildiriyor.
Eureka server ise bildirimden gelen book-service (isminden tanır) ismini çözümleyerek book service olduğunu tanıyor
    ve geri response olarak library service, evet bu book service bende kayıtlı localhost:8080 portuyla erişebilirsin diye response da bulunuyor.

Geriye dönen bu repsonse la birlikte library service book service feign client ile istek de bulunuyor

Feign Client:
    bir interface tanımdır.Bu interface tanımını kullanarak bbok service'in hangi endpointleri kullanacğını tanımladıktan sonra
    service çağırır gibi feign client a istek atıp book service'e ulaşabiliriz.
    feign client dan bir istek atıldığı zaman feign client otomatik olarak http isteğine çevirip restCall atıp geriye dönen json ı istenilen tipe convert edebiliriz
    Convert işlemini feign client kendisi hallediyor.

Not : eureka her istek de register'a işlem yapıyormu istekler tek tek yönleniyormu ? hayır cache yapısında saklıyor



Not: Service'in scale edilme işleminde scale edilmiş her service'in register işlemi eureka serverda bulunur.



***********************************************************************************************************************************************************************************************************


Strating MicroService

Kitaplık adında bir klasor açtık ve start.spring.io dan servicelerimizi ayrı ayrı oluşturup indirdik ve kitaplık klasorune attık.
Servicelerin dependency'lerinde netflix-eureka-client ve openFeign kullandık.
proje çalıştırmak için kitaplık klasorunu ide de açtık.
Böylece Kitaplık klasoru adı altında ayrı ayrı projelerımız(micro servicelerimiz oluştu)
    Bu işlemi bir new project açıp içerisinde new module diyerek de oluşturabilirdik.
    ve dependency leri parent a bağlayarak da biribirlerini kullanan bağlı dependencyler oluşturabilidrdik


Book Service oluşturulması:

book service 3 endpointe sahip bir servis.
Tüm kitapları getiren
isbn ile sorgulayarak kitap id sini getiren
id ile sorgulayarak kitabın bilgilerini getiren
          3 endpointe sahip
Gerekli service repository ve controller işlemleri yapıldı.
Db olarak h2 db kullanıldı. property leri applicarion.properties dosyasında verildi
Table oluşturmak için öncelikle spring.jpa.hibernate.ddl-auto=create-auto kullanıldı
daha sonraki çalıştırmada güncelleme işlemleri olacağı için spring.jpa.hibernate.ddl-auto=update kullanıldı.
db ye ilk eklene table bilgileri BookServiceApplication classında run methodunda uygulama ayağı kalkarken static olarak verildi


Library Service oluşturulması:

Eğer book service indeki db'nin içerisindeki book verilerini alıp direk library db ye atarsam library db şişer
    ve book service de bu model üzerine yapılan değişiklikten library service de etkilenir
        Not: Yapılan bir değişiklik diğer microservice de de değişim gerektiriyorsa o ms değildir







     */
}
