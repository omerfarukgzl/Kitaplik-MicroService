public class Notes {

/*                    ----------------
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
                ve Bookdb yi Library Db de tutsaydım o zaman bookDb ye ne gerek vardı?
            !! Bundan dolayı Library model de book listesi tutmak yerine string bookId listesi tuttuk ve book ıd lerine gore buraya ekleyerek (book-service den çekerek) verileri elde edeceğiz
                Service arasında id paylaşımı yapacağız

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

Not:
ResponseEntity<BookIdDto> getBookByIsbn(@PathVariable(value = "isbn") String isbn);

 value yazılmasının sebebi feign client PathVariable yı controller lardaki gibi isim aynı isse algıla işini yapamıyor
 value değeri verilmesi gerek


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







    *********************************  BookNotFoundException:  *******************************************

                                                                                 ==========================>  Create default value ( örneğin benim db de yoktur http://isbn.library.org/ 'a istek de bulunup doğrulanabilir)

            Stop process Inform User<===================Book Not Found Excepiton        new process
                    |
                    |
                    |                                                            ==========================>  Change the request path ( yada başka bir yere yönlendirilebilir)
                    |
                   \/
            Exception Handling                                                   ==========================> create another process
                    Isbn geçersizdir bilgisi


                                                                                 ==========================> throw exception



                                                    Ne olursa olsun book not found exception olursa program devam etmeli
                                                    ikisi aynı anda olamaz ya sağ taraf ya da sol taraf tercih edilir



Öreneğin biz isbn ile library'ye book eklemek istiyor olalım.

* library-service feign client register oldu

* book-service eureka server client register oldu

* isbn numarası ve library id ile library-service'e (addBookToLibrary) post methodu ile istek atıldı.

* eureka server library-service book-service in portunu ve name bilgisini döndü

* library-service feign client ile bu bilgileri kullanarak book-service ile haberleşti ve feign client aracılığı ile getBookByIsbn methodu ile eşleşerek istek atıldı

* book service den geriye ya bookId dönecek yada isbn no ile eşleşen book bulanamayıp BookNotFoundException hatası dönecek

* olması durumunda herşey doğru gerçekleşir ve id döner geri dönen id library userBook listesine edklenir ve db ye kaydedilir.

*** fakat BookNotFoundException hatası dönmesi durmunda db ye boş birşey kaydederim
     veya library service de if return id null sa kontrolu yaparsam library service patlar
        !bir hata sonucunda ms ler çalışmayı durdurusa bu ms mimari yaklaşımı değildir.( Hataya dayanıklı olmalı resilience)


    Öncelikle generalExceptionhandler oluşturuyoryuz(@RestControllerAdvice annottaioonu ile)

        @ExceptionHandler(LibraryNotFoundException.class)
        public ResponseEntity<?> handle(LibraryNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);

        }

        @ExceptionHandler(BookNotFoundException.class)
        public ResponseEntity<ExceptionMessage> handle(BookNotFoundException exception) {
            return new ResponseEntity<>(exception.getExceptionMessage(), HttpStatus.NOT_FOUND);
        }


        Fakat book bulanamaması  durumda NOT_FOUND yerine feign client decoder errorun yakalanması için  HttpStatus.resolve(exception.getExceptionMessage().status()) yazılır

        @ExceptionHandler(LibraryNotFoundException.class)
        public ResponseEntity<?> handle(LibraryNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);

        }

        @ExceptionHandler(BookNotFoundException.class)
        public ResponseEntity<ExceptionMessage> handle(BookNotFoundException exception) {
            return new ResponseEntity<>(exception.getExceptionMessage(), HttpStatus.resolve(exception.getExceptionMessage().status()));
        }


  Bundan dolayı feign clintdan alınan bu hatayı uygun bir reponeEntity'e çevirip kullanıcıya hata mesajı, bilgilendirme mesajı gönderilmelidir.)



    ******** RetreiveMessageErrorDecoder:  **********

Book-service den gelen book not found hatasını algılayan feign client ın döneceği hatayı özelliştirip kullanıcıya detaylı bilgi sunmalıyız

RetreiveMessageErrorDecoder feign client arayuzunun geliştirdiği ErrorDecoder ı implement eder.

 private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message = null;
        try (InputStream body = response.body().asInputStream()){
            message = new ExceptionMessage((String) response.headers().get("date").toArray()[0],
                    response.status(),
                    HttpStatus.resolve(response.status()).getReasonPhrase(),
                    IOUtils.toString(body, StandardCharsets.UTF_8),
                    response.request().url());

        } catch (IOException exception) {
            return new Exception(exception.getMessage());
        }
        switch (response.status()) {
            case 404:
                throw new BookNotFoundException(message);
            default:
                return errorDecoder.decode(methodKey, response);
        }
    }

    Öncelikle errorDecode nesnesi oluşturduk. new Default decode özelliğini kullanıyoruz(feign excepitonu excepitona çevirir)

    daha sonra gönderceğimiz error mesajlar için  ExcepitonMessage classı oluşturduk. yani hatada neler fırlatıcam onların classını oluşturduk.
        public record ExceptionMessage (String timestamp,int status,String error,String message, String path){}

daha sonra feignclient ın yakaladuğı hata mesajını eğer repsone status 404 ise elde ettiğim bu mesajı BookNotFoundExceptiona parametre olarak bastırıyorum ve exception handler bunu hata olduğu zaman yakalayıp clienta fırlatıyor
Exception handlerın yakalaması içinde HttpStatus.resolve(exception.getExceptionMessage().status()) yazılır
        @ExceptionHandler(LibraryNotFoundException.class)
        public ResponseEntity<?> handle(LibraryNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);

        }

        @ExceptionHandler(BookNotFoundException.class)
        public ResponseEntity<ExceptionMessage> handle(BookNotFoundException exception) {
            return new ResponseEntity<>(exception.getExceptionMessage(), HttpStatus.resolve(exception.getExceptionMessage().status()));
        }
eğer 404 den ayrı bir durum varsa default: decoder ın kendi sürümünü başlat


Ve bu errorDecoder ın çalışması için libraryApplicationPropperties de bean ı oluşturuduk.
oluşturmasaydık default olarak 500 ınternal server error alıcaktık

ve bu hataları feignlog seviyesinde de ayarlayabiliriz







*************************************************  Api gateway  *********************************************************************


Library service gelen istekler artmaya başladı ve istek yoğunluğunda 2 kişi daha istek yaptı ve app bu isteklere cevap vermeyecek duruma geldi
Bu durumda library service scale edilmesi , yeni bir instance oluşturulması gerekli.
fakat yeni bir instance nasıl oluşturum ? bu client dan gelen istek yeni instanmce'a nasıl ulaşır?

   öncelikle yeni bir instance oluşturulacağı zaman portunu değiştirmemiz gerkeir aynı porttan ikinci instance ayağı kalkmaz.
    ve bu yeni instance için  değişen port bilgisi clienta bildirilmeli
    bu noktada api gateway devreye girmeli.



***********Load Balancing**********
    Tüm gelen istekler apigateway'e gelir.
    ve gelen istek api gateway in içinde bulunduğu özellikden biri olan load balancing ile bu isteği hangi instance vermeliyimin kararını farklı algor göre verir ve yönlendirir(örneğin round robin)

**********Security Layer ***********
    ve apigateway in diğer özelliği olan security katmanı tüm ms lerin security katmanlarını içerebilir.
    istek gelir istekde token vardır  api getaway bağlı olduğu sunucudan tokenı doğrular ve isteği devam ettiri.


api gatewayde iyi bir cache alhoritması vardır her istekde db ye sormaz. önceden cache alır ve istek gelir gelmez cache den bakar varsa cache den verir yoksa db ye sorgu yapar ve cache e ekler.

************************** starting api gateway *******************

            yeni bir ms oluşturmak için yeni bir proje oluşturulur.
            start.spring.io dan gateway için dependency eklenir .Gateway dependecy 'ai eklenir.
            ve Bu gateway'i eureka ya da regster etmek istiyoruz.
            eureka'ya register olmasi için eureka discovery client dependency eklenir.


      ***********Not:***********
      yeni bir proje eklendiği zaman yanlızca file olarak gelir bunun spring boot projesi olması için
      sağ üstteki maven a tıklanır oradan + seçeneği seçilir ve yeni eklenen proje seçilir.
      ***********Not:***********

     bu projede application.yml dosyası kullanacağız. applicaiton.yml dosyası application.properties ile aynı işi yapar fakat yazımı farklıdır.

spring:
  application:
    name:gateway-service
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: book-service
          uri: lb://book-service
          predicates:
            - Path=/v1/book/**
        - id: library-service
          uri: lb://library-service
          predicates:
            - Path=/v1/library/**
#id: name
#uri:bağlanılacak servisin app name 'i        -
#predicates: hangi controller lar kullanılacak

#lb : load balancing demek eğer yazılamasydı instance lara bağlanamazdı


server:
  port:8888

eureka:
    client:
        serviceUrl:
            registerWithEureka: true
            defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
    instance:
        preferIpAddress: true


    bu gateway service eureka ya register olacak ve eureka da gözükecek.
    ve bu gateway service eureka da gözükeceği için clientlar bu gateway service e istek atacak.
    ve bu gateway service istekleri load balancing ile yönlendirecek.
    ve bu gateway service istekleri security layer ile kontrol edecek.
    ve bu gateway service istekleri cache algoritması ile kontrol edecek.


    Artık library service yapılan localhost:8080/v1/library isteği
        8888 portunda çalışan api gateway üzerinden localhost:8888/v1/library isteği ile yapılacak

    ve böylece ilk istekleri api gateway almış oldu.
    ve diğer serviceleri erişime kapattık. serviceleri dışardan erişime kapatmanın bir yolu random ip ataması yaparsak uygulama dısşından biri bulamaz ve istek atamaz
    Yanlızca ip bilenler bağlanabilir.



    Şimdi ise library service i scale edip scale dilen service i de ayağı kaldırmak istiyoruz .
     Fakat scale edilen service 'inde eureka ya register olmasını istiyoruz.
            eureka.instance.instance-id=${spring.application.name}:${random.value}



********************* Actuator  **********************

Actuator için dependency eklenir============>		<dependency>
                                                        <groupId>org.springframework.boot</groupId>
                                                        <artifactId>spring-boot-starter-actuator</artifactId>
                                                    </dependency>


Actuator ile Uygulamamızı izlemek, ölçümleri toplamak, trafiği anlamak veya veritabanımızın durumu izlenir.

manangment:
  endpoints:
    web:
      exposure:
        include: "*"

    bu kod ile tüm endpointleri açtık.
    api gateway application.yml dosyasında actuator configleri yapıldı.
    ve localhost:8888/actuator/ pathinde uygulamamızın tüm bilgilerini,metriklerini tüm endpointleri ile  görüntüleyebildik.


















 ***************************************************   Distributed Log Trace **************************************************************************************

Zipkin dağıtık olan logları tek bir yere toplayan özel bir uygulamadır.

 docker ile zipkin uygulamasını 9411 portunda çalıştırdık.http://localhost:9411/zipkin/ adresinden zipkin uygulamasına ulaşabildik.

    zipkin uygulamasını çalıştırdıktan sonra logların görülmesi için service de zipkin dependency eklenir.

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-sleuth</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-sleuth-zipkin</artifactId>
		</dependency>


ve application.properties dosyasına zipkin configleri yapılır.
spring.zipkin.base-url=http://localhost:9411



















*****************************************************************Spring Cloud Config Server****************************************************************************************

Neden Centralize configuration  ihtiyacı doğmuştur?
Ms lerin configuration'larını tek bir yerden yönetmek için doğmuştur.
Böylelikle service'in configları değişeceği zaman tek bir yerden değişiklik yapılabilir

uygulamamız örneğin library service için bir developer birde user instance 'ı çalıştırıyor ve
dev instance ında kullanılacak bir enviroment değişkeni user instance ında farklı olabilir.
örneğin dev için farklı db username password user için farklı ve default username password ile farklı db lere bağlanacak.
Bu yanlızca library service için olsun ve diğer ms ler için farklı env değişkenleri olsun ve toplamda 200 ms olsun
bu bizim için uğraş verici zorluktadır.
Bizde bu uğraşdan kurtulmak için Spring Cloud Config Server kullanırız. configleri tek bir yerden yöeneterek yaparız.



uygulama ayağı kalkarken library service spring cloud configden kendisiyle ilgili config dosyasını ondan alır ve conf yapar.

                    library service 8081/dev                book service 8081/dev
                    library service 8080/default            book service 8080/default
                                         /\                 /\
                                         |                  |
                                         |                  |
                                         |                  |
                                         |                  |
                                         |                  |
                                        Spring Cloud Config
                                                  /\
                                                  |
                                                  |
                                                Config     library-service-dev.properties
                                   classpath               library-service.properties
                                   git                     book-service-dev.properties
                                   vault                   book-service.properties
                      configler bunlar dasaklanabilir

properties de spring.profiles.active'i
# classpathden okumak istendiğinde native
# gitten okumak istendiğinde git

biz bu uygulamada git den okuyacağız



Spring cloud config server uygulaması oluşturulur.(start spring io)
    config-server artifact ve config server dependency eklendi

    Daha sonra ConfigServerApplication dosyasında main sınıfınfa config sever yapabilmek için @EnableConfigServer annotation eklendi.
       VE daha sonra @EnableDiscoveryClient ile eureka ya register ettik.(ve eureka client dependency ekledik)



    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>

    application.properties dosyasına config server configleri yapılır.

    spring.application.name=config-server
    server.port=8888
    spring.cloud.config.server.git.uri=




Spring cloud config server uygulaması uygulamalarımızın configlerini tek bir yerden yönetmemizi sağlar.



















 */











}
