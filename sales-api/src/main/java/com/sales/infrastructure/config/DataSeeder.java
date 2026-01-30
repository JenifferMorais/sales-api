package com.sales.infrastructure.config;

import com.sales.domain.auth.valueobject.Password;
import com.sales.domain.shared.port.EncryptionService;
import com.sales.infrastructure.persistence.auth.entity.UserEntity;
import com.sales.infrastructure.persistence.customer.entity.CustomerEntity;
import com.sales.infrastructure.persistence.product.entity.ProductEntity;
import com.sales.infrastructure.persistence.sale.entity.SaleEntity;
import com.sales.infrastructure.persistence.sale.entity.SaleItemEntity;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;

@ApplicationScoped
public class DataSeeder {

    private static final Logger LOG = Logger.getLogger(DataSeeder.class);

    @Inject
    EntityManager em;

    @Inject
    EncryptionService encryptionService;

    @Inject
    @ConfigProperty(name = "app.seed.enabled", defaultValue = "false")
    boolean seedEnabled;

    @Transactional
    public void loadData(@Observes StartupEvent event) {

        if (!seedEnabled) {
            LOG.info("Data seeding disabled (app.seed.enabled=false). Skipping.");
            return;
        }

        Long userCount = em.createQuery("SELECT COUNT(u) FROM UserEntity u", Long.class).getSingleResult();
        if (userCount > 0) {
            LOG.info("Database already contains data. Skipping seed.");
            return;
        }

        LOG.info("Seeding database with test data...");
        LOG.warn("SECURITY WARNING: Running with test data seeds. Disable in production!");

        seedCustomers();
        seedUsers();
        seedProducts();
        seedSales();

        LOG.info("Database seeding completed!");
    }

    private void seedCustomers() {

        CustomerEntity customer1 = new CustomerEntity();
        customer1.setCode("CUST001");
        customer1.setFullName("John Silva Santos");
        customer1.setMotherName("Maria Silva Santos");
        customer1.setCpf("12345678901");
        customer1.setRg("MG1234567");
        customer1.setZipCode("30130100");
        customer1.setStreet("Av. Afonso Pena");
        customer1.setNumber("1500");
        customer1.setComplement("Apt 101");
        customer1.setNeighborhood("Centro");
        customer1.setCity("Belo Horizonte");
        customer1.setState("MG");
        customer1.setBirthDate(LocalDate.of(1990, 5, 15));
        customer1.setCellPhone("31987654321");
        customer1.setEmail("john.silva@email.com");
        em.persist(customer1);

        CustomerEntity customer2 = new CustomerEntity();
        customer2.setCode("CUST002");
        customer2.setFullName("Maria Oliveira Costa");
        customer2.setMotherName("Ana Oliveira Costa");
        customer2.setCpf("98765432109");
        customer2.setRg("SP9876543");
        customer2.setZipCode("01310100");
        customer2.setStreet("Av. Paulista");
        customer2.setNumber("1000");
        customer2.setNeighborhood("Bela Vista");
        customer2.setCity("São Paulo");
        customer2.setState("SP");
        customer2.setBirthDate(LocalDate.of(1985, 8, 20));
        customer2.setCellPhone("11987654321");
        customer2.setEmail("maria.oliveira@email.com");
        em.persist(customer2);

        CustomerEntity customer3 = new CustomerEntity();
        customer3.setCode("CUST003");
        customer3.setFullName("Carlos Roberto Mendes");
        customer3.setMotherName("Joana Mendes");
        customer3.setCpf("11122233344");
        customer3.setRg("RJ1122334");
        customer3.setZipCode("20040020");
        customer3.setStreet("Av. Rio Branco");
        customer3.setNumber("156");
        customer3.setNeighborhood("Centro");
        customer3.setCity("Rio de Janeiro");
        customer3.setState("RJ");
        customer3.setBirthDate(LocalDate.of(1992, 3, 10));
        customer3.setCellPhone("21987654321");
        customer3.setEmail("carlos.mendes@email.com");
        em.persist(customer3);

        CustomerEntity customer4 = new CustomerEntity();
        customer4.setCode("CUST004");
        customer4.setFullName("Ana Paula Ferreira");
        customer4.setMotherName("Rita Ferreira");
        customer4.setCpf("55566677788");
        customer4.setRg("MG5566778");
        customer4.setZipCode("30190100");
        customer4.setStreet("Rua dos Carijós");
        customer4.setNumber("500");
        customer4.setNeighborhood("Centro");
        customer4.setCity("Belo Horizonte");
        customer4.setState("MG");
        customer4.setBirthDate(LocalDate.of(1988, 11, 25));
        customer4.setCellPhone("31998765432");
        customer4.setEmail("ana.ferreira@email.com");
        em.persist(customer4);

        CustomerEntity customer5 = new CustomerEntity();
        customer5.setCode("CUST005");
        customer5.setFullName("Pedro Henrique Lima");
        customer5.setMotherName("Beatriz Lima");
        customer5.setCpf("99988877766");
        customer5.setRg("RS9988776");
        customer5.setZipCode("90010000");
        customer5.setStreet("Av. Borges de Medeiros");
        customer5.setNumber("2500");
        customer5.setNeighborhood("Centro");
        customer5.setCity("Porto Alegre");
        customer5.setState("RS");
        customer5.setBirthDate(LocalDate.of(1995, 1, 5));
        customer5.setCellPhone("51987654321");
        customer5.setEmail("pedro.lima@email.com");
        em.persist(customer5);

        CustomerEntity customer6 = new CustomerEntity();
        customer6.setCode("CUST006");
        customer6.setFullName("Juliana Rodrigues Alves");
        customer6.setMotherName("Sandra Rodrigues");
        customer6.setCpf("44455566677");
        customer6.setRg("PR4455667");
        customer6.setZipCode("80010000");
        customer6.setStreet("Rua XV de Novembro");
        customer6.setNumber("800");
        customer6.setNeighborhood("Centro");
        customer6.setCity("Curitiba");
        customer6.setState("PR");
        customer6.setBirthDate(LocalDate.of(1987, 7, 18));
        customer6.setCellPhone("41987654321");
        customer6.setEmail("juliana.alves@email.com");
        em.persist(customer6);

        CustomerEntity customer7 = new CustomerEntity();
        customer7.setCode("CUST007");
        customer7.setFullName("Fernando Souza Pereira");
        customer7.setMotherName("Lucia Souza");
        customer7.setCpf("33344455566");
        customer7.setRg("BA3344556");
        customer7.setZipCode("40010000");
        customer7.setStreet("Av. Sete de Setembro");
        customer7.setNumber("300");
        customer7.setNeighborhood("Centro");
        customer7.setCity("Salvador");
        customer7.setState("BA");
        customer7.setBirthDate(LocalDate.of(1991, 4, 22));
        customer7.setCellPhone("71987654321");
        customer7.setEmail("fernando.pereira@email.com");
        em.persist(customer7);

        CustomerEntity customer8 = new CustomerEntity();
        customer8.setCode("CUST008");
        customer8.setFullName("Tatiana Costa Martins");
        customer8.setMotherName("Helena Costa");
        customer8.setCpf("22233344455");
        customer8.setRg("CE2233445");
        customer8.setZipCode("60010000");
        customer8.setStreet("Av. Beira Mar");
        customer8.setNumber("1200");
        customer8.setNeighborhood("Meireles");
        customer8.setCity("Fortaleza");
        customer8.setState("CE");
        customer8.setBirthDate(LocalDate.of(1989, 9, 30));
        customer8.setCellPhone("85987654321");
        customer8.setEmail("tatiana.martins@email.com");
        em.persist(customer8);

        CustomerEntity customer9 = new CustomerEntity();
        customer9.setCode("CUST009");
        customer9.setFullName("Ricardo Gomes Silva");
        customer9.setMotherName("Patricia Gomes");
        customer9.setCpf("66677788899");
        customer9.setRg("SC6677889");
        customer9.setZipCode("88010000");
        customer9.setStreet("Av. Beira Mar Norte");
        customer9.setNumber("2000");
        customer9.setNeighborhood("Centro");
        customer9.setCity("Florianópolis");
        customer9.setState("SC");
        customer9.setBirthDate(LocalDate.of(1993, 12, 8));
        customer9.setCellPhone("48987654321");
        customer9.setEmail("ricardo.gomes@email.com");
        em.persist(customer9);

        CustomerEntity customer10 = new CustomerEntity();
        customer10.setCode("CUST010");
        customer10.setFullName("Camila Andrade Santos");
        customer10.setMotherName("Vanessa Andrade");
        customer10.setCpf("77788899900");
        customer10.setRg("DF7788990");
        customer10.setZipCode("70010000");
        customer10.setStreet("Esplanada dos Ministérios");
        customer10.setNumber("100");
        customer10.setNeighborhood("Zona Cívico-Administrativa");
        customer10.setCity("Brasília");
        customer10.setState("DF");
        customer10.setBirthDate(LocalDate.of(1994, 6, 14));
        customer10.setCellPhone("61987654321");
        customer10.setEmail("camila.santos@email.com");
        em.persist(customer10);

        LOG.info("Customers seeded: 10");
    }

    /**
     * DEVELOPMENT ONLY - Seeds test users with known credentials.
     *
     * SECURITY WARNING: This method creates users with hardcoded passwords.
     * - DO NOT use in production environments
     * - Only for local development and testing
     * - Disable or remove before deploying to production
     *
     * All seeded users use password: Test@123
     */
    private void seedUsers() {

        String hashedPassword = Password.fromPlainText("Test@123").getHashedValue();

        UserEntity user1 = new UserEntity();
        user1.setCustomerCode("CUST001");
        user1.setEmail("john.silva@email.com");
        user1.setPassword(hashedPassword);
        user1.setActive(true);
        em.persist(user1);

        UserEntity user2 = new UserEntity();
        user2.setCustomerCode("CUST002");
        user2.setEmail("maria.oliveira@email.com");
        user2.setPassword(hashedPassword);
        user2.setActive(true);
        em.persist(user2);

        UserEntity user3 = new UserEntity();
        user3.setCustomerCode("CUST003");
        user3.setEmail("carlos.mendes@email.com");
        user3.setPassword(hashedPassword);
        user3.setActive(true);
        em.persist(user3);

        UserEntity user4 = new UserEntity();
        user4.setCustomerCode("CUST004");
        user4.setEmail("ana.ferreira@email.com");
        user4.setPassword(hashedPassword);
        user4.setActive(false);
        em.persist(user4);

        UserEntity user5 = new UserEntity();
        user5.setCustomerCode("CUST005");
        user5.setEmail("pedro.lima@email.com");
        user5.setPassword(hashedPassword);
        user5.setActive(true);
        em.persist(user5);

        UserEntity user6 = new UserEntity();
        user6.setCustomerCode("CUST006");
        user6.setEmail("juliana.alves@email.com");
        user6.setPassword(hashedPassword);
        user6.setActive(true);
        em.persist(user6);

        UserEntity user7 = new UserEntity();
        user7.setCustomerCode("CUST007");
        user7.setEmail("fernando.pereira@email.com");
        user7.setPassword(hashedPassword);
        user7.setActive(true);
        em.persist(user7);

        UserEntity user8 = new UserEntity();
        user8.setCustomerCode("CUST008");
        user8.setEmail("tatiana.martins@email.com");
        user8.setPassword(hashedPassword);
        user8.setActive(true);
        em.persist(user8);

        UserEntity user9 = new UserEntity();
        user9.setCustomerCode("CUST009");
        user9.setEmail("ricardo.gomes@email.com");
        user9.setPassword(hashedPassword);
        user9.setActive(true);
        em.persist(user9);

        UserEntity user10 = new UserEntity();
        user10.setCustomerCode("CUST010");
        user10.setEmail("camila.santos@email.com");
        user10.setPassword(hashedPassword);
        user10.setActive(true);
        em.persist(user10);

        LOG.warn("DEVELOPMENT: Users seeded with test credentials (Email: john.silva@email.com, Password: Test@123)");
    }

    private void seedProducts() {

        ProductEntity product1 = new ProductEntity();
        product1.setCode("PROD001");
        product1.setName("Batom Matte Longa Duração");
        product1.setType("Lábios");
        product1.setDetails("Batom matte com alta pigmentação e longa duração - Cor: Vermelho Clássico");
        product1.setWeight(new BigDecimal("0.050"));
        product1.setPurchasePrice(new BigDecimal("18.00"));
        product1.setSalePrice(new BigDecimal("35.00"));
        product1.setHeight(new BigDecimal("8.00"));
        product1.setWidth(new BigDecimal("2.00"));
        product1.setDepth(new BigDecimal("2.00"));
        product1.setDestinationVehicle("Todos os tipos de pele");
        product1.setStockQuantity(200);
        em.persist(product1);

        ProductEntity product2 = new ProductEntity();
        product2.setCode("PROD002");
        product2.setName("Base Líquida HD Cobertura Total");
        product2.setType("Rosto");
        product2.setDetails("Base líquida alta cobertura acabamento natural - Tom: Médio Bege");
        product2.setWeight(new BigDecimal("0.120"));
        product2.setPurchasePrice(new BigDecimal("45.00"));
        product2.setSalePrice(new BigDecimal("89.00"));
        product2.setHeight(new BigDecimal("12.00"));
        product2.setWidth(new BigDecimal("4.00"));
        product2.setDepth(new BigDecimal("4.00"));
        product2.setDestinationVehicle("Pele mista a oleosa");
        product2.setStockQuantity(150);
        em.persist(product2);

        ProductEntity product3 = new ProductEntity();
        product3.setCode("PROD003");
        product3.setName("Paleta de Sombras Nude");
        product3.setType("Olhos");
        product3.setDetails("Paleta com 12 cores nude e marrom - Acabamento matte e shimmer");
        product3.setWeight(new BigDecimal("0.180"));
        product3.setPurchasePrice(new BigDecimal("55.00"));
        product3.setSalePrice(new BigDecimal("120.00"));
        product3.setHeight(new BigDecimal("2.00"));
        product3.setWidth(new BigDecimal("15.00"));
        product3.setDepth(new BigDecimal("10.00"));
        product3.setDestinationVehicle("Todos os tipos de pele");
        product3.setStockQuantity(80);
        em.persist(product3);

        ProductEntity product4 = new ProductEntity();
        product4.setCode("PROD004");
        product4.setName("Máscara de Cílios Volume Extremo");
        product4.setType("Olhos");
        product4.setDetails("Máscara para cílios com efeito volume e alongamento - À prova d'água");
        product4.setWeight(new BigDecimal("0.080"));
        product4.setPurchasePrice(new BigDecimal("25.00"));
        product4.setSalePrice(new BigDecimal("52.00"));
        product4.setHeight(new BigDecimal("15.00"));
        product4.setWidth(new BigDecimal("2.50"));
        product4.setDepth(new BigDecimal("2.50"));
        product4.setDestinationVehicle("Todos os tipos de cílios");
        product4.setStockQuantity(180);
        em.persist(product4);

        ProductEntity product5 = new ProductEntity();
        product5.setCode("PROD005");
        product5.setName("Blush Compacto Cor de Rosa");
        product5.setType("Rosto");
        product5.setDetails("Blush compacto com acabamento acetinado - Cor: Rosa Pêssego");
        product5.setWeight(new BigDecimal("0.100"));
        product5.setPurchasePrice(new BigDecimal("22.00"));
        product5.setSalePrice(new BigDecimal("45.00"));
        product5.setHeight(new BigDecimal("2.00"));
        product5.setWidth(new BigDecimal("8.00"));
        product5.setDepth(new BigDecimal("8.00"));
        product5.setDestinationVehicle("Todos os tons de pele");
        product5.setStockQuantity(120);
        em.persist(product5);

        ProductEntity product6 = new ProductEntity();
        product6.setCode("PROD006");
        product6.setName("Primer Facial Matte");
        product6.setType("Rosto");
        product6.setDetails("Primer facial que controla oleosidade e aumenta a duração da maquiagem");
        product6.setWeight(new BigDecimal("0.090"));
        product6.setPurchasePrice(new BigDecimal("35.00"));
        product6.setSalePrice(new BigDecimal("68.00"));
        product6.setHeight(new BigDecimal("10.00"));
        product6.setWidth(new BigDecimal("4.00"));
        product6.setDepth(new BigDecimal("4.00"));
        product6.setDestinationVehicle("Pele oleosa a mista");
        product6.setStockQuantity(100);
        em.persist(product6);

        ProductEntity product7 = new ProductEntity();
        product7.setCode("PROD007");
        product7.setName("Delineador Líquido Preto Intenso");
        product7.setType("Olhos");
        product7.setDetails("Delineador líquido com aplicador de ponta fina - Secagem rápida");
        product7.setWeight(new BigDecimal("0.060"));
        product7.setPurchasePrice(new BigDecimal("15.00"));
        product7.setSalePrice(new BigDecimal("32.00"));
        product7.setHeight(new BigDecimal("13.00"));
        product7.setWidth(new BigDecimal("2.00"));
        product7.setDepth(new BigDecimal("2.00"));
        product7.setDestinationVehicle("Todos os tipos de pele");
        product7.setStockQuantity(160);
        em.persist(product7);

        ProductEntity product8 = new ProductEntity();
        product8.setCode("PROD008");
        product8.setName("Pó Compacto Translúcido");
        product8.setType("Rosto");
        product8.setDetails("Pó compacto translúcido que controla o brilho e sela a maquiagem");
        product8.setWeight(new BigDecimal("0.110"));
        product8.setPurchasePrice(new BigDecimal("28.00"));
        product8.setSalePrice(new BigDecimal("55.00"));
        product8.setHeight(new BigDecimal("2.50"));
        product8.setWidth(new BigDecimal("9.00"));
        product8.setDepth(new BigDecimal("9.00"));
        product8.setDestinationVehicle("Todos os tons de pele");
        product8.setStockQuantity(140);
        em.persist(product8);

        ProductEntity product9 = new ProductEntity();
        product9.setCode("PROD009");
        product9.setName("Corretivo Líquido Alta Cobertura");
        product9.setType("Rosto");
        product9.setDetails("Corretivo líquido para olheiras e imperfeições - Tom: Claro Médio");
        product9.setWeight(new BigDecimal("0.070"));
        product9.setPurchasePrice(new BigDecimal("32.00"));
        product9.setSalePrice(new BigDecimal("62.00"));
        product9.setHeight(new BigDecimal("10.00"));
        product9.setWidth(new BigDecimal("3.00"));
        product9.setDepth(new BigDecimal("3.00"));
        product9.setDestinationVehicle("Todos os tipos de pele");
        product9.setStockQuantity(130);
        em.persist(product9);

        ProductEntity product10 = new ProductEntity();
        product10.setCode("PROD010");
        product10.setName("Gloss Labial Hidratante");
        product10.setType("Lábios");
        product10.setDetails("Gloss labial com efeito brilho intenso e hidratação - Cor: Nude Rosado");
        product10.setWeight(new BigDecimal("0.055"));
        product10.setPurchasePrice(new BigDecimal("12.00"));
        product10.setSalePrice(new BigDecimal("28.00"));
        product10.setHeight(new BigDecimal("11.00"));
        product10.setWidth(new BigDecimal("2.50"));
        product10.setDepth(new BigDecimal("2.50"));
        product10.setDestinationVehicle("Todos os tipos de lábios");
        product10.setStockQuantity(190);
        em.persist(product10);

        ProductEntity product11 = new ProductEntity();
        product11.setCode("PROD011");
        product11.setName("Iluminador Líquido Dourado");
        product11.setType("Rosto");
        product11.setDetails("Iluminador líquido com partículas douradas para realçar a pele");
        product11.setWeight(new BigDecimal("0.085"));
        product11.setPurchasePrice(new BigDecimal("38.00"));
        product11.setSalePrice(new BigDecimal("72.00"));
        product11.setHeight(new BigDecimal("12.00"));
        product11.setWidth(new BigDecimal("3.50"));
        product11.setDepth(new BigDecimal("3.50"));
        product11.setDestinationVehicle("Todos os tons de pele");
        product11.setStockQuantity(95);
        em.persist(product11);

        ProductEntity product12 = new ProductEntity();
        product12.setCode("PROD012");
        product12.setName("Lápis de Olho Preto À Prova D'água");
        product12.setType("Olhos");
        product12.setDetails("Lápis de olho preto cremoso e resistente à água");
        product12.setWeight(new BigDecimal("0.040"));
        product12.setPurchasePrice(new BigDecimal("10.00"));
        product12.setSalePrice(new BigDecimal("22.00"));
        product12.setHeight(new BigDecimal("14.00"));
        product12.setWidth(new BigDecimal("1.00"));
        product12.setDepth(new BigDecimal("1.00"));
        product12.setDestinationVehicle("Todos os tipos de pele");
        product12.setStockQuantity(210);
        em.persist(product12);

        LOG.info("Products seeded: 12");
    }

    /**
     * Criptografa cardNumber para armazenamento seguro.
     * Usado apenas no seed de dados de teste.
     */
    private String encryptCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return null;
        }
        return encryptionService.encrypt(cardNumber);
    }

    private void seedSales() {

        SaleEntity sale1 = new SaleEntity();
        sale1.setCode("SALE001");
        sale1.setCustomerCode("CUST001");
        sale1.setCustomerName("John Silva Santos");
        sale1.setSellerCode("SELLER001");
        sale1.setSellerName("Vendedor Sistema");
        sale1.setPaymentMethod("Cartão de Crédito");
        sale1.setCardNumber(encryptCardNumber("4532015112830366")); // Teste: Visa
        sale1.setAmountPaid(new BigDecimal("195.00"));

        SaleItemEntity item1_1 = new SaleItemEntity();
        item1_1.setProductCode("PROD001");
        item1_1.setProductName("Batom Matte Longa Duração");
        item1_1.setQuantity(2);
        item1_1.setUnitPrice(new BigDecimal("35.00"));
        sale1.addItem(item1_1);

        SaleItemEntity item1_2 = new SaleItemEntity();
        item1_2.setProductCode("PROD003");
        item1_2.setProductName("Paleta de Sombras Nude");
        item1_2.setQuantity(1);
        item1_2.setUnitPrice(new BigDecimal("120.00"));
        sale1.addItem(item1_2);

        SaleItemEntity item1_3 = new SaleItemEntity();
        item1_3.setProductCode("PROD005");
        item1_3.setProductName("Blush Compacto Cor de Rosa");
        item1_3.setQuantity(1);
        item1_3.setUnitPrice(new BigDecimal("45.00"));
        sale1.addItem(item1_3);

        em.persist(sale1);

        SaleEntity sale2 = new SaleEntity();
        sale2.setCode("SALE002");
        sale2.setCustomerCode("CUST002");
        sale2.setCustomerName("Maria Oliveira Costa");
        sale2.setSellerCode("SELLER001");
        sale2.setSellerName("Vendedor Sistema");
        sale2.setPaymentMethod("PIX");
        sale2.setAmountPaid(new BigDecimal("178.00"));

        SaleItemEntity item2_1 = new SaleItemEntity();
        item2_1.setProductCode("PROD002");
        item2_1.setProductName("Base Líquida HD Cobertura Total");
        item2_1.setQuantity(2);
        item2_1.setUnitPrice(new BigDecimal("89.00"));
        sale2.addItem(item2_1);

        em.persist(sale2);

        SaleEntity sale3 = new SaleEntity();
        sale3.setCode("SALE003");
        sale3.setCustomerCode("CUST003");
        sale3.setCustomerName("Carlos Roberto Mendes");
        sale3.setSellerCode("SELLER001");
        sale3.setSellerName("Vendedor Sistema");
        sale3.setPaymentMethod("Dinheiro");
        sale3.setAmountPaid(new BigDecimal("196.00"));

        SaleItemEntity item3_1 = new SaleItemEntity();
        item3_1.setProductCode("PROD004");
        item3_1.setProductName("Máscara de Cílios Volume Extremo");
        item3_1.setQuantity(3);
        item3_1.setUnitPrice(new BigDecimal("52.00"));
        sale3.addItem(item3_1);

        SaleItemEntity item3_2 = new SaleItemEntity();
        item3_2.setProductCode("PROD007");
        item3_2.setProductName("Delineador Líquido Preto Intenso");
        item3_2.setQuantity(2);
        item3_2.setUnitPrice(new BigDecimal("32.00"));
        sale3.addItem(item3_2);

        em.persist(sale3);

        SaleEntity sale4 = new SaleEntity();
        sale4.setCode("SALE004");
        sale4.setCustomerCode("CUST005");
        sale4.setCustomerName("Pedro Henrique Lima");
        sale4.setSellerCode("SELLER001");
        sale4.setSellerName("Vendedor Sistema");
        sale4.setPaymentMethod("Cartão de Débito");
        sale4.setCardNumber(encryptCardNumber("5425233430109903")); // Teste: Mastercard
        sale4.setAmountPaid(new BigDecimal("206.00"));

        SaleItemEntity item4_1 = new SaleItemEntity();
        item4_1.setProductCode("PROD006");
        item4_1.setProductName("Primer Facial Matte");
        item4_1.setQuantity(2);
        item4_1.setUnitPrice(new BigDecimal("68.00"));
        sale4.addItem(item4_1);

        SaleItemEntity item4_2 = new SaleItemEntity();
        item4_2.setProductCode("PROD001");
        item4_2.setProductName("Batom Matte Longa Duração");
        item4_2.setQuantity(2);
        item4_2.setUnitPrice(new BigDecimal("35.00"));
        sale4.addItem(item4_2);

        em.persist(sale4);

        SaleEntity sale5 = new SaleEntity();
        sale5.setCode("SALE005");
        sale5.setCustomerCode("CUST006");
        sale5.setCustomerName("Juliana Rodrigues Alves");
        sale5.setSellerCode("SELLER001");
        sale5.setSellerName("Vendedor Sistema");
        sale5.setPaymentMethod("Cartão de Crédito");
        sale5.setCardNumber(encryptCardNumber("378282246310005")); // Teste: Amex
        sale5.setAmountPaid(new BigDecimal("282.00"));

        SaleItemEntity item5_1 = new SaleItemEntity();
        item5_1.setProductCode("PROD003");
        item5_1.setProductName("Paleta de Sombras Nude");
        item5_1.setQuantity(2);
        item5_1.setUnitPrice(new BigDecimal("120.00"));
        sale5.addItem(item5_1);

        SaleItemEntity item5_2 = new SaleItemEntity();
        item5_2.setProductCode("PROD010");
        item5_2.setProductName("Gloss Labial Hidratante");
        item5_2.setQuantity(1);
        item5_2.setUnitPrice(new BigDecimal("28.00"));
        sale5.addItem(item5_2);

        SaleItemEntity item5_3 = new SaleItemEntity();
        item5_3.setProductCode("PROD012");
        item5_3.setProductName("Lápis de Olho Preto À Prova D'água");
        item5_3.setQuantity(2);
        item5_3.setUnitPrice(new BigDecimal("22.00"));
        sale5.addItem(item5_3);

        em.persist(sale5);

        SaleEntity sale6 = new SaleEntity();
        sale6.setCode("SALE006");
        sale6.setCustomerCode("CUST007");
        sale6.setCustomerName("Fernando Souza Pereira");
        sale6.setSellerCode("SELLER001");
        sale6.setSellerName("Vendedor Sistema");
        sale6.setPaymentMethod("PIX");
        sale6.setAmountPaid(new BigDecimal("213.00"));

        SaleItemEntity item6_1 = new SaleItemEntity();
        item6_1.setProductCode("PROD002");
        item6_1.setProductName("Base Líquida HD Cobertura Total");
        item6_1.setQuantity(1);
        item6_1.setUnitPrice(new BigDecimal("89.00"));
        sale6.addItem(item6_1);

        SaleItemEntity item6_2 = new SaleItemEntity();
        item6_2.setProductCode("PROD009");
        item6_2.setProductName("Corretivo Líquido Alta Cobertura");
        item6_2.setQuantity(2);
        item6_2.setUnitPrice(new BigDecimal("62.00"));
        sale6.addItem(item6_2);

        em.persist(sale6);

        SaleEntity sale7 = new SaleEntity();
        sale7.setCode("SALE007");
        sale7.setCustomerCode("CUST008");
        sale7.setCustomerName("Tatiana Costa Martins");
        sale7.setSellerCode("SELLER001");
        sale7.setSellerName("Vendedor Sistema");
        sale7.setPaymentMethod("Cartão de Crédito");
        sale7.setCardNumber(encryptCardNumber("6011111111111117")); // Teste: Discover
        sale7.setAmountPaid(new BigDecimal("340.00"));

        SaleItemEntity item7_1 = new SaleItemEntity();
        item7_1.setProductCode("PROD008");
        item7_1.setProductName("Pó Compacto Translúcido");
        item7_1.setQuantity(2);
        item7_1.setUnitPrice(new BigDecimal("55.00"));
        sale7.addItem(item7_1);

        SaleItemEntity item7_2 = new SaleItemEntity();
        item7_2.setProductCode("PROD005");
        item7_2.setProductName("Blush Compacto Cor de Rosa");
        item7_2.setQuantity(3);
        item7_2.setUnitPrice(new BigDecimal("45.00"));
        sale7.addItem(item7_2);

        SaleItemEntity item7_3 = new SaleItemEntity();
        item7_3.setProductCode("PROD011");
        item7_3.setProductName("Iluminador Líquido Dourado");
        item7_3.setQuantity(2);
        item7_3.setUnitPrice(new BigDecimal("72.00"));
        sale7.addItem(item7_3);

        em.persist(sale7);

        SaleEntity sale8 = new SaleEntity();
        sale8.setCode("SALE008");
        sale8.setCustomerCode("CUST009");
        sale8.setCustomerName("Ricardo Gomes Silva");
        sale8.setSellerCode("SELLER001");
        sale8.setSellerName("Vendedor Sistema");
        sale8.setPaymentMethod("Dinheiro");
        sale8.setAmountPaid(new BigDecimal("158.00"));

        SaleItemEntity item8_1 = new SaleItemEntity();
        item8_1.setProductCode("PROD004");
        item8_1.setProductName("Máscara de Cílios Volume Extremo");
        item8_1.setQuantity(2);
        item8_1.setUnitPrice(new BigDecimal("52.00"));
        sale8.addItem(item8_1);

        SaleItemEntity item8_2 = new SaleItemEntity();
        item8_2.setProductCode("PROD012");
        item8_2.setProductName("Lápis de Olho Preto À Prova D'água");
        item8_2.setQuantity(1);
        item8_2.setUnitPrice(new BigDecimal("22.00"));
        sale8.addItem(item8_2);

        SaleItemEntity item8_3 = new SaleItemEntity();
        item8_3.setProductCode("PROD007");
        item8_3.setProductName("Delineador Líquido Preto Intenso");
        item8_3.setQuantity(1);
        item8_3.setUnitPrice(new BigDecimal("32.00"));
        sale8.addItem(item8_3);

        em.persist(sale8);

        SaleEntity sale9 = new SaleEntity();
        sale9.setCode("SALE009");
        sale9.setCustomerCode("CUST010");
        sale9.setCustomerName("Camila Andrade Santos");
        sale9.setSellerCode("SELLER001");
        sale9.setSellerName("Vendedor Sistema");
        sale9.setPaymentMethod("PIX");
        sale9.setAmountPaid(new BigDecimal("216.00"));

        SaleItemEntity item9_1 = new SaleItemEntity();
        item9_1.setProductCode("PROD011");
        item9_1.setProductName("Iluminador Líquido Dourado");
        item9_1.setQuantity(3);
        item9_1.setUnitPrice(new BigDecimal("72.00"));
        sale9.addItem(item9_1);

        em.persist(sale9);

        SaleEntity sale10 = new SaleEntity();
        sale10.setCode("SALE010");
        sale10.setCustomerCode("CUST001");
        sale10.setCustomerName("John Silva Santos");
        sale10.setSellerCode("SELLER001");
        sale10.setSellerName("Vendedor Sistema");
        sale10.setPaymentMethod("Cartão de Débito");
        sale10.setCardNumber(encryptCardNumber("4532015112830366")); // Teste: Visa
        sale10.setAmountPaid(new BigDecimal("371.00"));

        SaleItemEntity item10_1 = new SaleItemEntity();
        item10_1.setProductCode("PROD003");
        item10_1.setProductName("Paleta de Sombras Nude");
        item10_1.setQuantity(2);
        item10_1.setUnitPrice(new BigDecimal("120.00"));
        sale10.addItem(item10_1);

        SaleItemEntity item10_2 = new SaleItemEntity();
        item10_2.setProductCode("PROD006");
        item10_2.setProductName("Primer Facial Matte");
        item10_2.setQuantity(1);
        item10_2.setUnitPrice(new BigDecimal("68.00"));
        sale10.addItem(item10_2);

        SaleItemEntity item10_3 = new SaleItemEntity();
        item10_3.setProductCode("PROD009");
        item10_3.setProductName("Corretivo Líquido Alta Cobertura");
        item10_3.setQuantity(1);
        item10_3.setUnitPrice(new BigDecimal("62.00"));
        sale10.addItem(item10_3);

        SaleItemEntity item10_4 = new SaleItemEntity();
        item10_4.setProductCode("PROD001");
        item10_4.setProductName("Batom Matte Longa Duração");
        item10_4.setQuantity(1);
        item10_4.setUnitPrice(new BigDecimal("35.00"));
        sale10.addItem(item10_4);

        em.persist(sale10);

        LOG.info("Sales seeded: 10");
    }
}
