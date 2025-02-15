// package com.letrify.app;

// import com.letrify.app.model.Document;
// import com.letrify.app.model.Portfolio;
// import com.letrify.app.model.PortfolioDocument;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// import com.letrify.app.repository.PortfolioDocumentRepository;

// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.assertThat;

// @DataJpaTest
// class PortfolioDocumentRepositoryTest {

//     @Autowired
//     private PortfolioDocumentRepository portfolioDocumentRepository;

//     @Test
//     void testSaveAndFindByPortfolioId() {
//         PortfolioDocument portfolioDocument = new PortfolioDocument();
        
//         Portfolio portfolio = new Portfolio();
//         portfolio.setId(1L); // Simula una cartera con ID 1 (usando una instancia ficticia)
        
//         Document document = new Document();
//         document.setId(2L); // Simula un documento con ID 2
        
//         portfolioDocument.setPortfolio(portfolio);
//         portfolioDocument.setDocument(document);
        
//         // Guardar la relación
//         portfolioDocumentRepository.save(portfolioDocument);
    
//         // Buscar las relaciones por el ID de la cartera
//         List<PortfolioDocument> results = portfolioDocumentRepository.findByPortfolioId(1L);
    
//         // Verificar que se haya encontrado la relación
//         assertThat(results).isNotEmpty();
//         assertThat(results.get(0).getDocument().getId()).isEqualTo(2L);
//     }

//     @Test
//     void testFindByPortfolioIdAndDocumentId() {
//         PortfolioDocument portfolioDocument = new PortfolioDocument();
        
//         Portfolio portfolio = new Portfolio();
//         portfolio.setId(3L);
        
//         Document document = new Document();
//         document.setId(4L);
        
//         portfolioDocument.setPortfolio(portfolio);
//         portfolioDocument.setDocument(document);
        
//         portfolioDocumentRepository.save(portfolioDocument);
    
//         Optional<PortfolioDocument> result = portfolioDocumentRepository.findByPortfolioIdAndDocumentId(3L, 4L);
//         assertThat(result).isPresent();
//     }

//     @Test
//     void testCountByPortfolioId() {
//         Portfolio portfolio = new Portfolio();
//         portfolio.setId(5L);
        
//         Document document1 = new Document();
//         document1.setId(6L);
        
//         Document document2 = new Document();
//         document2.setId(7L);
        
//         PortfolioDocument portfolioDocument1 = new PortfolioDocument();
//         portfolioDocument1.setPortfolio(portfolio);
//         portfolioDocument1.setDocument(document1);
        
//         PortfolioDocument portfolioDocument2 = new PortfolioDocument();
//         portfolioDocument2.setPortfolio(portfolio);
//         portfolioDocument2.setDocument(document2);
        
//         portfolioDocumentRepository.save(portfolioDocument1);
//         portfolioDocumentRepository.save(portfolioDocument2);
    
//         long count = portfolioDocumentRepository.countByPortfolioId(5L);
//         assertThat(count).isEqualTo(2);
//     }
// }
