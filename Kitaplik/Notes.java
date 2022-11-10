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
            !! Bundan dolayı Library model de book listesi tutmak yerine string bbokId listesi tuttuk ve book ıd lerine gore buraya ekleyerek (book-service den çekerek) verileri elde edeceğiz

    Daha sonra service ksımında kutuphane getirilmesi methodunda library-service feign clint ile haberleşeceği için feign client oluşturduk.
        Adı genel olarak hangi service ulaşılacaksa o servicein adı ile başlar. Bir interface dir
    Bu interfaci bir feign client haline getiren annotation @FeignClient dır.
    @FeignClient(name="book-service",path="v1/book" ) name="book-service" sayesinde eurekadan sunucu bilgilerini çekip hangi path den çalışacağından sonra çalışıyor



    *********************************  Service:  *******************************************


     public LibraryDto getAllBooksInLibraryById(String id) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id: " + id));

        LibraryDto libraryDto = new LibraryDto(library.getId(),library.getUserBook().stream().map(bookServiceClient::getBookById).map(ResponseEntity::getBody).collect(Collectors.toList()));
        return libraryDto;
    }
        Bu methodda controllerdan gelen library içindeki tüm kitapları listeleme isteği çalışmaktadır.
        Methoda gönderilen istek parametresi ise library id sidir.
        db den id ye bağlı olan library bulunur.
        Daha sonra bulunan library verileri library Dto ya çevirilip kullanıcıya gönderilir.
        Dto converter işleminde library içindeki userBook listesi alınır
        ve daha sonra userbookListesindeki id lerini kullanarak sırası ile bookservice den gelen o i de ye sahip book ların detayları BookDto olarak oluştur.
        stream().map().map().collect()

        library.getUserBook().stream().map(bookServiceClient::getBookById).map(ResponseEntity::getBody).collect(Collectors.toList()))

            ikinci map kullanmamızın sebebi tek map li olduğu zaman client dan bize responseEntity (json dönecektir) bu jsonun BookDto ya dönüşmesi için
                ResponseEntity den dönen jsonun get body sine map işlemi yapmamız gerekir.




    public LibraryDto createLibrary() {
        Library newLibrary = libraryRepository.save(new Library());
        return new LibraryDto(newLibrary.getId(),null);
    }
     methodu ise yeni library oluşturur.(içindeki book listesi başta nulldur sonra eklenirse bu null değişir)



    public void addBookToLibrary(AddBookRequest request) {
        String bookId = bookServiceClient.getBookByIsbn(request.getIsbn()).getBody().getId();

        Library library = libraryRepository.findById(request.getId())
                .orElseThrow(() -> new LibraryNotFoundException("Library could not found by id: " + request.getId()));

        library.getUserBook()
                .add(bookId);

        libraryRepository.save(library);
    }
        Methodu Library'e book ekleme işlemini yapar.Bu ekleme işleminde eklenecek olan library id si ve library'e  eklenecek olan book id si json formatında pyt isteği olarak gönderilir.
         gönderilen library id den o  library db den bulunur. Dah sonra library-service micro service i book-service microservisinden eklenecek olan kitabı id parametresiyle bulur.
         ve bulunan library'nin bookUser listesine bulunan book id verisi eklenir. ve db ye kaydedilir.




    public List<String> getAllLibraries() {

        return libraryRepository.findAll()
                .stream()
                .map(l -> l.getId())
                .collect(Collectors.toList());
    }

        Bu methodda ise tüm library lerin id leri listelenir.




    *********************************  BookServiceClient:  *******************************************

    İnterface dir. Methodların gövdeleri yazılmaz. Book service ms in aynı controller methodalrı alınıp içi boş bir şekilde koyulabilir
    Library service ms in book service ms e istek atarak haberleşmesini sağlar
    istek atan feignclient dir ( LibraryServiceApplication 'a  @ FeignClientEnable annottaionu eklenir.)
    istek alan eureakeclien dir(BookServiceApplication'a @ EurekaServerClientEnable annotationu eklenir)

   @GetMapping("/isbn/{isbn}")
   // @CircuitBreaker(name = "getBookByIsbnCircuitBreaker", fallbackMethod = "getBookFallback")
    ResponseEntity<BookIdDto> getBookByIsbn(@PathVariable(value = "isbn") String isbn);

    methodu library-service den gelen isbn no su ile book bilgisini book service ms den elde etmeye yarar.


    @GetMapping("/book/{bookId}")
   // @CircuitBreaker(name = "getBookByIdCircuitBreaker", fallbackMethod = "getBookByIdFallback")
    ResponseEntity<BookDto> getBookById(@PathVariable(value = "bookId") String bookId);

    methodu library-service den gelen id no su ile book bilgisini book service ms den elde etmeye yarar.




    *********************************  Dto:  *******************************************

    AddBookRequest =======> kullanıcının put isteği gönderirken json formatında gönderilecek olan request classı

    BookDto        =======> Book Service de BookDto var zaten neden onu kullanmadık?
                            BookService de olan dto book-service in cliente göstermek istediği dto dur
                            Fakat biz library-service'ne istek attığımızda direk olarak book-service ms den alınan bookDto yu göstemek istemeyibiliriz
                            Bundan dolayı ms den aldığımız bilgiler için de dto oluşturmalıyız.(Veriler aynı olsa bile)

    BookIdDto      =======> BookDto için yazılanlar BookIdDto içinde geçerlidir.

    LibraryDto     =======> Library entisinden alınan veriler clienta döndürüleceği zaman bu dto ya dönüştürülü gönderilir.
                            !! Library içerisinde String tipinde bir list varken neden bu dto da BookDto(Library-service bookDto) tipinde bir liste var.
                                Çünkü Library entity sinde db de saklanacağı için Book listesi tutmak istemedik. Bunun sebebi db yi şişirecekti.
                                    Bunun yerine book id string listesi tuttuk ve kullanıcıya response edileceği zaman LibraryDto ile book id ler yerine bookDto tipinde liste dmnerek kullanıcya library içerisindeki book bilgileirinide dönmüş olduk.



















     */
}
